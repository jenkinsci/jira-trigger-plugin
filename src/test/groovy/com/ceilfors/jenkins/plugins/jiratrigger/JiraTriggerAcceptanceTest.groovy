package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.integration.*
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import spock.lang.IgnoreRest
import spock.lang.Specification

import static JiraCommentTrigger.DEFAULT_COMMENT
import static com.ceilfors.jenkins.plugins.jiratrigger.integration.JiraSetupRule.CUSTOM_FIELD_NAME
/**
 * @author ceilfors
 */
class JiraTriggerAcceptanceTest extends Specification {

    JenkinsRunner jenkins = new JenkinsRunner()

    @Rule
    RuleChain ruleChain = RuleChain
            .outerRule(new JulLogLevelRule())
            .around(jenkins)
            .around(new JiraSetupRule(jenkins))
            .around(
            new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    jira = new RealJiraRunner(jenkins)
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
        project.addParameterMapping("jenkins_description", "description")
        project.addParameterMapping("jenkins_key", "key")

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduledWithParameter("simpleJob", [
                "jenkins_description": "Dummy issue description",
                "jenkins_key"        : issueKey
        ])
    }

    def 'Trigger job with built-in field when an issue is updated'() {
        given:
        def issueKey = jira.createIssue("Dummy issue description")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.addParameterMapping("jenkins_description", "description")
        project.addParameterMapping("jenkins_key", "key")

        when:
        jira.updateDescription(issueKey, "New description")

        then:
        jenkins.buildShouldBeScheduledWithParameter("job", [
                "jenkins_description": "New description",
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

    def 'Triggers a job when an issue is updated and the issue matches the JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.setJqlFilter('type=task and description~"New description" and status="To Do"')

        when:
        jira.updateDescription(issueKey, "New description")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Does not trigger a job when an issue is updated and the issue does not match the JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.setJqlFilter('type=task and description~"New description" and status="Done"')

        when:
        jira.updateDescription(issueKey, "New description")

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

    def 'Triggers a job when issue status is updated to Done'() {
        given:
        def issueKey = jira.createIssue("original description")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.addJiraFieldChangelogMatcher("status", "Done")

        when:
        jira.updateStatus(issueKey, "Done")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Triggers a job when issue status is updated from To Do to Done'() {
        given:
        def issueKey = jira.createIssue("original description")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.addJiraFieldChangelogMatcher("status", "To Do", "Done")

        when:
        jira.updateStatus(issueKey, "Done")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Does not trigger a job when issue status is updated to In Progress whilst Done is expected'() {
        given:
        def issueKey = jira.createIssue("original description")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.addJiraFieldChangelogMatcher("status", "Done")

        when:
        jira.updateStatus(issueKey, "In Progress")

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Does not trigger a job when issue status is updated from In Progress to Done whilst the original status should have been To Do'() {
        given:
        def issueKey = jira.createIssue("original description")
        jira.updateStatus(issueKey, "In Progress")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.addJiraFieldChangelogMatcher("status", "To Do", "Done")

        when:
        jira.updateStatus(issueKey, "Done")

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Triggers a job when a custom field is updated'() {
        given:
        def issueKey = jira.createIssue("original description")
        def project = jenkins.createJiraChangelogTriggeredProject("job")
        project.addCustomFieldChangelogMatcher(CUSTOM_FIELD_NAME, "Barclays")

        when:
        jira.updateCustomField(issueKey, CUSTOM_FIELD_NAME, "Barclays")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    // Check CauseAction in JenkinsRunner to differentiate trigger? Can be retrieved at Queue.Item.getActions()

    // ** Incremental features: **
    // Empty value in oldValue and newValue is ambiguous. Add a checkbox to differentiate if user wants empty value or optional
    // Unit test changelog matcher, toString and fromString can be null!
    // help files
    // wiki
    // -- 0.2.0 --

    // Add comment - when changelog is added
    // Add comment - when there is a comment pattern that matches, but no jobs have been triggered
    // Add comment - Visibility to jira-administrators
    // Add comment - Visibility must be configured in global configuration i.e. role/group
    // Don't process comment from the user configured in Jenkins due to potential infinite loop?
    // -- 0.3.0 --

    // Register webhook from Jenkins configuration page
    // Make AcceptanceTest independent of JIRA
    // Run CI in CloudBees Jenkins
    // --- 1.0.0 ---

    // void method in JrjcJiraClient should be async. Be careful on concurrency issues in this test case.
    // How to enable JenkinsRule as ClassRule to make the build faster
    // JiraTriggerCause should contain issue key and link
    // Override UncaughtExceptionHandler in Acceptance Test to catch Exception, especially when webhook is configured wrongly and Acceptance test don't see any error
    // Form Validation in Global Config by hitting JIRA
    // Check SequentialExecutionQueue that is used by GitHubWebHook
    // Should JiraWebhook be RootAction rather than UnprotectedRootAction? Check out RequirePostWithGHHookPayload
    // Translate JiraTriggerException to error messages
}
