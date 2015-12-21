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

    def 'Trigger simple job when a comment is created'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createFreeStyleProject("simplejob")

        when:
        jira.addComment(issueKey, "a comment")

        then:
        jenkins.buildShouldBeScheduled("simplejob")
    }

    // Incremental build
    // Trigger a job when a comment matches a pattern
    // Trigger a job with parameter with a comment is created
    // Trigger a job with custom field in JIRA to a parameter
}
