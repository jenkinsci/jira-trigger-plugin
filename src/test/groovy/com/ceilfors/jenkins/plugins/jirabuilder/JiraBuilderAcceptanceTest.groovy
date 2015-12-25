package com.ceilfors.jenkins.plugins.jirabuilder
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import java.util.logging.Level
/**
 * @author ceilfors
 */
class JiraBuilderAcceptanceTest extends Specification {

    def Jira jira = new RcarzJira()

    @Rule JenkinsRunner jenkins = new JenkinsRunner()
    @Rule JulLogLevelRule julLogLevelRule = new JulLogLevelRule(Level.FINEST)

    def setup() {
        jira.deleteAllWebHooks()
    }

    @Unroll
    def 'Trigger simple job when a comment is created'() {
        given:
        jira.registerWebHook(jenkins.webHookUrl)
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
        jira.registerWebHook(jenkins.webHookUrl)
        def issueKey = jira.createIssue("Dummy issue description")
        jenkins.createJiraTriggeredProject("simplejob", "description")

        when:
        jira.addComment(issueKey, "a comment")

        then:
        jenkins.buildShouldBeScheduled("simplejob")
        jenkins.buildTriggeredWithParameter("simplejob", ["description": "Dummy issue description"])
    }

    def 'Job is triggered when a comment matches the comment pattern'() {
        given:
        jira.registerWebHook(jenkins.webHookUrl)
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderCommentFilter("job", ".*jiratrigger.*")

        when:
        jira.addComment(issueKey, "bla jiratrigger bla")

        then:
        jenkins.buildShouldBeScheduled("job")
    }

    def 'Job is not triggered when a comment does not match the comment pattern'() {
        given:
        jira.registerWebHook(jenkins.webHookUrl)
        def issueKey = jira.createIssue()
        jenkins.createJiraTriggeredProject("job")
        jenkins.setJiraBuilderCommentFilter("job", ".*jiratrigger.*")

        when:
        jira.addComment(issueKey, "bla bla bla")

        then:
        jenkins.buildShouldNotBeScheduled("job")
    }

    // Incremental features:
    // Trigger a job with custom field in JIRA to a parameter
    // Trigger a job when a comment matches a pattern
    // Trigger a job with parameter with a comment is created
    // Trigger a job when matches JQL
    // Updated comment ?
    // Duplicate issue in jql configuration
    // Should JiraWebHook be RootAction rather than UnprotectedRootAction? Check out RequirePostWithGHHookPayload
}
