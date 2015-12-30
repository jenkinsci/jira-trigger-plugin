package com.ceilfors.jenkins.plugins.jirabuilder
import com.ceilfors.jenkins.plugins.jirabuilder.jira.Jira
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JrjcJiraClient
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import java.util.logging.Level
/**
 * @author ceilfors
 */
class JiraBuilderAcceptanceTest extends Specification {

    def Jira jira = new JrjcJiraClient()

    @Rule JenkinsRunner jenkins = new JenkinsRunner()
    @Rule JulLogLevelRule julLogLevelRule = new JulLogLevelRule(Level.FINEST)

    def setupSpec() {
        // Initialize JIRA in Jenkins Global Configuration
    }

    def setup() {
        jira.deleteAllWebhooks()
    }

    @Unroll
    def 'Trigger simple job when a comment is created'() {
        given:
        jira.registerWebhook(jenkins.webhookUrl)
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject(jobName)

        when:
        jira.addComment(issueKey, comment)

        then:
        jenkins.buildShouldBeScheduled(jobName)

        where:
        jobName      | comment
        "simplejob"  | "a comment"
        "other job"  | "arbitrary comment"
    }

    def 'Trigger job with built-in field when a comment is created'() {
        given:
        jira.registerWebhook(jenkins.webhookUrl)
        def issueKey = jira.createIssue("Dummy issue description")
        jenkins.createJiraTriggeredProject("simplejob", "jenkins_description", "jenkins_key")
        jenkins.addParameterMapping("simplejob", "jenkins_description", "fields.description")
        jenkins.addParameterMapping("simplejob", "jenkins_key", "key")

        when:
        jira.addComment(issueKey, "a comment")

        then:
        jenkins.buildShouldBeScheduled("simplejob")
        jenkins.buildTriggeredWithParameter("simplejob", [
                "jenkins_description": "Dummy issue description",
                "jenkins_key": issueKey
        ])
    }

    def 'Job is triggered when a comment matches the comment pattern'() {
        given:
        jira.registerWebhook(jenkins.webhookUrl)
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
        jira.registerWebhook(jenkins.webhookUrl)
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderCommentPattern("job", ".*jiratrigger.*")

        when:
        jira.addComment(issueKey, "bla bla bla")

        then:
        jenkins.buildShouldNotBeScheduled("job")
    }

    def 'Job is triggered when the issue matches JQL filter'() {
        given:
        jira.registerWebhook(jenkins.webhookUrl)
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
        jira.registerWebhook(jenkins.webhookUrl)
        def issueKey = jira.createIssue("dummy description")
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderJqlFilter("job", 'type=task and status="Done"')

        when:
        jira.addComment(issueKey, "comment body")

        then:
        jenkins.buildShouldNotBeScheduled("job")
    }

    // ** Incremental features: **
    // Make JIRA configurable including the webhook URL from Jenkins
    // Help message
    // Webhook 'h' is small letter.
    // Updated comment ?
    // Should JiraWebhook be RootAction rather than UnprotectedRootAction? Check out RequirePostWithGHHookPayload
}
