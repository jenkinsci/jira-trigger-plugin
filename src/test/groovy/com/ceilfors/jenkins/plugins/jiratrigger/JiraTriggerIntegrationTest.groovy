package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.changelog.CustomFieldChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.JiraFieldChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.integration.JenkinsRunner
import com.ceilfors.jenkins.plugins.jiratrigger.integration.JulLogLevelRule
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException
import hudson.model.AbstractBuild
import hudson.model.FreeStyleProject
import hudson.model.Queue
import hudson.security.GlobalMatrixAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm
import jenkins.model.GlobalConfiguration
import org.acegisecurity.context.SecurityContextHolder
import org.junit.Rule
import org.junit.rules.RuleChain
import org.jvnet.hudson.test.Issue
import spock.lang.Specification

/**
 * @author ceilfors
 */
class JiraTriggerIntegrationTest extends Specification {

    JenkinsRunner jenkins = new JenkinsRunner()

    @Rule
    RuleChain ruleChain = RuleChain
            .outerRule(new JulLogLevelRule())
            .around(jenkins)

    def 'Global configuration round trip'() {
        JiraTriggerGlobalConfiguration before = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        before.jiraRootUrl = "localhost:2990/jira"
        before.jiraUsername = "admin"
        before.jiraPassword = "admin"
        before.save()

        jenkins.configRoundtrip()
        JiraTriggerGlobalConfiguration after = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        jenkins.assertEqualBeans(before, after, "jiraRootUrl,jiraUsername,jiraPassword")
    }

    def 'JiraChangelogTrigger configuration round trip'() {
        FreeStyleProject p = jenkins.createFreeStyleProject()
        JiraChangelogTrigger before = new JiraChangelogTrigger()
        before.changelogMatchers = [
                new JiraFieldChangelogMatcher("status", "new value", "old value", true, true),
                new CustomFieldChangelogMatcher("custom field", "new value", "old value", true, true)
        ]
        p.addTrigger(before)

        jenkins.submit(jenkins.createWebClient().getPage(p, "configure").getFormByName("config"));

        JiraChangelogTrigger after = p.getTrigger(JiraChangelogTrigger)

        jenkins.assertEqualBeans(before, after, "changelogMatchers");
    }

    def 'Comment pattern by default must not be empty'() {
        when:
        def project = jenkins.createJiraCommentTriggeredProject("job")

        then:
        project.commentPatternShouldNotBeEmpty()
    }

    def 'Injects environment variable to scheduled build'() {
        given:
        jenkins.createJiraCommentTriggeredProject("job")
        jenkins.jiraTriggerExecutor.setQuietPeriod(0)

        when:
        def scheduledProjects = jenkins.jiraTriggerExecutor.scheduleBuilds(
                TestUtils.createIssue("TEST-1234"),
                TestUtils.createComment(JiraCommentTrigger.DEFAULT_COMMENT))

        then:
        scheduledProjects.size() != 0
        Queue.Item item = scheduledProjects[0].queueItem
        Map environment
        if (item == null) {
            environment = scheduledProjects[0].getBuildByNumber(1).environment
        } else {
            environment = (item.getFuture().startCondition.get() as AbstractBuild).environment
        }
        environment.get("JIRA_ISSUE_KEY") == "TEST-1234"
    }

    @Issue("JENKINS-34135")
    def 'Should be able to schedule jobs with anonymous user does not have read permission'() {
        setup:
        jenkins.createJiraCommentTriggeredProject("job")

        when: "Security is enabled in Jenkins and anonymous access is taken off with empty GlobalMatrixAuthorizationStrategy settings"
        jenkins.instance.securityRealm = new HudsonPrivateSecurityRealm(true)
        jenkins.instance.authorizationStrategy = new GlobalMatrixAuthorizationStrategy()

        then: "Item should still visible before we make the thread anonymous"
        jenkins.instance.allItems.size() == 1

        when: "Thread is made anonymous"
        SecurityContextHolder.clearContext()

        then: "Item should now disappear"
        jenkins.instance.allItems.size() == 0

        when: "Trigger item"
        def scheduledProjects = jenkins.jiraTriggerExecutor.scheduleBuilds(
                TestUtils.createIssue("TEST-1234"),
                TestUtils.createComment(JiraCommentTrigger.DEFAULT_COMMENT))

        then: "Item is scheduled"
        scheduledProjects.size() != 0
        scheduledProjects[0].queueItem.task.name == "job"
    }

    @Issue('JENKINS-34301')
    def 'Should not swallow Jenkins security exception'() {
        setup:
        jenkins.createJiraCommentTriggeredProject("job")
        def webClient = jenkins.createWebClient()

        when: 'Security is enabled in Jenkins and anonymous access is taken off with empty GlobalMatrixAuthorizationStrategy settings'
        jenkins.instance.securityRealm = new HudsonPrivateSecurityRealm(true)
        jenkins.instance.authorizationStrategy = new GlobalMatrixAuthorizationStrategy()
        webClient.getPage(webClient.contextPath)

        then:
        FailingHttpStatusCodeException exception = thrown(FailingHttpStatusCodeException)
        exception.statusCode == 403
    }
}
