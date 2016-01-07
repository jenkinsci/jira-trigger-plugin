package com.ceilfors.jenkins.plugins.jirabuilder

import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JrjcJiraClient
import jenkins.model.GlobalConfiguration
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import spock.lang.Specification
import spock.lang.Unroll

import java.util.logging.Level

/**
 * @author ceilfors
 */
class JiraBuilderAcceptanceTest extends Specification {

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
                    JiraBuilderGlobalConfiguration configuration = GlobalConfiguration.all().get(JiraBuilderGlobalConfiguration)
                    configuration.jiraRootUrl = jiraRootUrl
                    configuration.jiraUsername = jiraUsername
                    configuration.jiraPassword = jiraPassword
                    jira = new JrjcJiraClient(configuration)
                    jira.deleteAllWebhooks()
                    jira.registerWebhook(jenkins.webhookUrl)
                }
            })

    JiraClient jira

    @Unroll
    def 'Trigger simple job when a comment is created'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject(jobName)

        when:
        jira.addComment(issueKey, comment)

        then:
        jenkins.buildShouldBeScheduled(jobName)

        where:
        jobName     | comment
        "simplejob" | "a comment"
        "other job" | "arbitrary comment"
    }

    def 'Trigger job with built-in field when a comment is created'() {
        given:
        def issueKey = jira.createIssue("Dummy issue description")
        jenkins.createJiraTriggeredProject("simplejob", "jenkins_description", "jenkins_key")
        jenkins.addParameterMapping("simplejob", "jenkins_description", "fields.description")
        jenkins.addParameterMapping("simplejob", "jenkins_key", "key")

        when:
        jira.addComment(issueKey, "a comment")

        then:
        jenkins.buildShouldBeScheduledWithParameter("simplejob", [
                "jenkins_description": "Dummy issue description",
                "jenkins_key"        : issueKey
        ])
    }

    def 'Job is triggered when a comment matches the comment pattern'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderCommentPattern("job", ".*jiratrigger.*")

        when:
        jira.addComment(issueKey, "bla jiratrigger bla")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Job is not triggered when a comment does not match the comment pattern'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderCommentPattern("job", ".*jiratrigger.*")

        when:
        jira.addComment(issueKey, "bla bla bla")

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Job is triggered when the issue matches JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderJqlFilter("job", 'type=task and description~"dummy description" and status="To Do"')

        when:
        jira.addComment(issueKey, "comment body")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Job is not triggered when the issue does not match JQL filter'() {
        given:
        def issueKey = jira.createIssue("dummy description")
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderJqlFilter("job", 'type=task and status="Done"')

        when:
        jira.addComment(issueKey, "comment body")

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Should be able to hit JIRA when setting up JIRA global configuration from UI'() {
        given:
        def issueKey = jira.createIssue("Dummy issue description")
        jenkins.createJiraTriggeredProject("simplejob", "jenkins_description")
        jenkins.addParameterMapping("simplejob", "jenkins_description", "fields.description")

        when:
        jenkins.setJiraBuilderGlobalConfig(jiraRootUrl, jiraUsername, jiraPassword)
        jira.addComment(issueKey, "a comment")

        then:
        jenkins.buildShouldBeScheduledWithParameter("simplejob", [
                "jenkins_description": "Dummy issue description"
        ])
    }


    // ** Incremental features: **
    // Help message
    // Add default comment pattern to prevent all jobs being triggered without configuration
    // Should JiraWebhook be RootAction rather than UnprotectedRootAction? Check out RequirePostWithGHHookPayload
    // Check if logs are working
    // Fix JiraClient createIssue(), it will be never be used by this plugin
    // Fix JiraClient registerWebhook(), it should auto re-register without deleting webhook.
    // Fix JiraClient delete webhook should work by searching for base URL
    // --- 1.0.0 ---

    // Make AcceptanceTest independent of JIRA
    // Run CI in CloudBees Jenkins
    // --- 1.0.1 ---

    // Register webhook from Jenkins configuration page
    // Add comment back to JIRA when there is a comment pattern that matches, but no jobs have been triggered
    // Override UncaughtExceptionHandler in Acceptance Test to catch Exception, especially when webhook is configured wrongly and Acceptance test don't see any error
    // Form Validation in Global Config by hitting JIRA
    // Check SequentialExecutionQueue that is used by GitHubWebHook
    // Updated comment ?
}
