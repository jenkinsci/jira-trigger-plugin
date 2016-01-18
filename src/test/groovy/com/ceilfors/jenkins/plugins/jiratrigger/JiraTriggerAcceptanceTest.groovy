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

    def 'Trigger simple job when a comment is created'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def "Should reply back to JIRA when a build is scheduled"() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")

        when:
        jenkins.setJiraCommentReply(true)
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jira.shouldBeNotifiedWithComment(issueKey, "job")
    }

    def 'Trigger job with built-in field when a comment is created'() {
        given:
        def issueKey = jira.createIssue("Dummy issue description")
        jenkins.createJiraTriggeredProject("simpleJob", "jenkins_description", "jenkins_key")
        jenkins.addParameterMapping("simpleJob", "jenkins_description", "fields.description")
        jenkins.addParameterMapping("simpleJob", "jenkins_key", "key")

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
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraTriggerCommentPattern("job", ".*jira trigger.*")

        when:
        jira.addComment(issueKey, "bla jira trigger bla")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Job is not triggered when a comment does not match the comment pattern'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraTriggerCommentPattern("job", ".*jira trigger.*")

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Job is triggered when the issue matches JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraTriggerJqlFilter("job", 'type=task and description~"dummy description" and status="To Do"')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Job is not triggered when the issue does not match JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraTriggerJqlFilter("job", 'type=task and status="Done"')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Jobs is triggered when JIRA configuration is set from the UI'() {
        given:
        def issueKey = jira.createIssue("Dummy issue description")
        jenkins.createJiraTriggeredProject("simpleJob", "jenkins_description")
        jenkins.setJiraTriggerJqlFilter("simpleJob", 'type=task and description~"dummy description" and status="To Do"')

        when:
        jenkins.setJiraTriggerGlobalConfig(jiraRootUrl, jiraUsername, jiraPassword)
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled("simpleJob")
    }

    def 'Comment pattern by default must not be empty'() {
        when:
        jenkins.createJiraTriggeredProject("job")

        then:
        jenkins.triggerCommentPatternShouldNotBeEmpty("job")
    }

    // ** Incremental features: **
    // Trigger job when issue is updated - all
    // Remove comment_created webhook type as preparation
    // Usage of BlockingQueue is duplicated at JenkinsRunner and JiraRunner
    // Trigger job when issue is updated - filter by field
    // Trigger job when issue is updated - filter by from and to value
    // -- 0.2.0 --

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
