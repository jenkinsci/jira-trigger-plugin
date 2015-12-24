package com.ceilfors.jenkins.plugins.jirabuilder
import org.junit.Rule
import spock.lang.Specification
/**
 * @author ceilfors
 */
class JiraBuilderAcceptanceTest extends Specification {

    def Jira jira = new RcarzJira()

    @Rule
    def JenkinsRunner jenkins = new JenkinsRunner()

    def setup() {
        jira.deleteAllWebHooks()
    }

    def 'Trigger simple job when a comment is created'() {
        given:
        jira.registerWebHook(jenkins.webHookUrl)
        def issueKey = jira.createIssue()
        jenkins.createFreeStyleProject("simplejob")

        when:
        jira.addComment(issueKey, "a comment")

        then:
        jenkins.buildShouldBeScheduled("simplejob")
    }

    def 'Trigger job with built-in field when a comment is created'() {
        given:
        jira.registerWebHook(jenkins.webHookUrl)
        def issueKey = jira.createIssue("Dummy issue description")
        jenkins.createFreeStyleProjectWithParameter("simplejob", "description")

        when:
        jira.addComment(issueKey, "a comment")

        then:
        jenkins.buildShouldBeScheduled("simplejob")
        jenkins.buildTriggeredWithParameter("simplejob", "Dummy issue description")
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
