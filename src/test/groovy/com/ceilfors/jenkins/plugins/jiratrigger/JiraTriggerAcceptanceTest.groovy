package com.ceilfors.jenkins.plugins.jiratrigger
import jenkins.model.GlobalConfiguration
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import spock.lang.Specification

import java.util.logging.Level

import static JiraCommentTrigger.DEFAULT_COMMENT
/**
 * @author ceilfors
 */
class JiraTriggerAcceptanceTest extends Specification {

    String jiraRootUrl = "http://localhost:2990/jira"
    String jiraUsername = "admin"
    String jiraPassword = "admin"

    JenkinsRunner jenkins = new JenkinsRunner()

    @Rule
    RuleChain ruleChain = RuleChain
            .outerRule(new JulLogLevelRule(Level.FINEST))
            .around(jenkins)
            .around(
            new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    JiraTriggerGlobalConfiguration configuration = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
                    configuration.jiraRootUrl = jiraRootUrl
                    configuration.jiraUsername = jiraUsername
                    configuration.jiraPassword = jiraPassword
                    configuration.save()
                    jira = new RealJiraRunner(jenkins, configuration)
                    jira.deleteAllWebhooks()
                    jira.registerWebhook(jenkins.webhookUrl.replace("localhost", "10.0.2.2"))
                }
            })

    JiraRunner jira

    def 'Should trigger a job when a comment is added'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraCommentTriggeredProject("job")

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Should trigger a job when an issue is updated'() {
        given:
        def issueKey = jira.createIssue("Original description")
        jenkins.createJiraChangelogTriggeredProject("job")

        when:
        jira.updateDescription(issueKey, "New description")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def "Should reply back to JIRA when a build is scheduled"() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraCommentTriggeredProject("job")

        when:
        jenkins.setJiraCommentReply(true)
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jira.shouldBeNotifiedWithComment(issueKey, "job")
    }

    def 'Trigger job with built-in field when a comment is created'() {
        given:
        def issueKey = jira.createIssue("Dummy issue description")
        def project = jenkins.createJiraCommentTriggeredProject("simpleJob", "jenkins_description", "jenkins_key")
        project.addParameterMapping("jenkins_description", "fields.description")
        project.addParameterMapping("jenkins_key", "key")

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduledWithParameter("simpleJob", [
                "jenkins_description": "Dummy issue description",
                "jenkins_key"        : issueKey
        ])
    }

    def 'Job is triggered when a comment matches the comment pattern'() {
        given:
        def issueKey = jira.createIssue()
        def project = jenkins.createJiraCommentTriggeredProject("job")
        project.setCommentPattern(".*jira trigger.*")

        when:
        jira.addComment(issueKey, "bla jira trigger bla")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Job is not triggered when a comment does not match the comment pattern'() {
        given:
        def issueKey = jira.createIssue()
        def project = jenkins.createJiraCommentTriggeredProject("job")
        project.setCommentPattern(".*jira trigger.*")

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Job is triggered when a comment is added and the issue matches the JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        def project = jenkins.createJiraCommentTriggeredProject("job")
        project.setJqlFilter('type=task and description~"dummy description" and status="To Do"')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Job is not triggered when the issue does not match JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        def project = jenkins.createJiraCommentTriggeredProject("job")
        project.setJqlFilter('type=task and status="Done"')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Jobs is triggered when JIRA configuration is set from the UI'() {
        given:
        def issueKey = jira.createIssue("Dummy issue description")
        def project = jenkins.createJiraCommentTriggeredProject("simpleJob", "jenkins_description")
        project.setJqlFilter('type=task and description~"dummy description" and status="To Do"')

        when:
        jenkins.setJiraTriggerGlobalConfig(jiraRootUrl, jiraUsername, jiraPassword)
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled("simpleJob")
    }

    def 'Comment pattern by default must not be empty'() {
        when:
        def project = jenkins.createJiraCommentTriggeredProject("job")

        then:
        project.commentPatternShouldNotBeEmpty()
    }

    // what happen when an issue contains changelog and event?
    // What happen if a job have two of the trigger configured? Maybe only allow one type of trigger by using dropdownDescriptorSelector?
    // Think should trigger once even though two triggers are configured
    // Check CauseAction in JenkinsRunner to differentiate trigger? Can be retrieved at Queue.Item.getActions()

    // ** Incremental features: **
    // Trigger job when issue is updated - filter by field
    // Trigger job when issue is updated - filter by from and to value
    // -- 0.2.0 --

    // Add comment - when changelog is added
    // Add comment - when there is a comment pattern that matches, but no jobs have been triggered
    // Add comment - Visibility to jira-administrators
    // Add comment - Visibility must be configured in global configuration i.e. role/group
    // Don't process comment from the user configured in Jenkins due to potential infinite loop?
    // -- 0.3.0 --

    // Register webhook from Jenkins configuration page
    // Document log names in wiki
    // Make AcceptanceTest independent of JIRA
    // Run CI in CloudBees Jenkins
    // --- 1.0.0 ---

    // Jira returned result should be cached. Use Guava.
    // void method in JrjcJiraClient should be async. Be careful on concurrency issues in this test case.
    // How to enable JenkinsRule as ClassRule to make the build faster
    // JiraTriggerCause should contain issue key and link
    // Override UncaughtExceptionHandler in Acceptance Test to catch Exception, especially when webhook is configured wrongly and Acceptance test don't see any error
    // Form Validation in Global Config by hitting JIRA
    // Check SequentialExecutionQueue that is used by GitHubWebHook
    // Should JiraWebhook be RootAction rather than UnprotectedRootAction? Check out RequirePostWithGHHookPayload
    // Translate JiraTriggerException to error messages
}
