package com.ceilfors.jenkins.plugins.jiratrigger.integration

import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import spock.lang.Specification

import static com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger.DEFAULT_COMMENT

/**
 * @author ceilfors
 */
class JiraEndToEndAcceptanceTest extends Specification {

    JenkinsRunner jenkins = new JenkinsRunner()

    @Rule
    RuleChain ruleChain = RuleChain
            .outerRule(new JulLogLevelRule())
            .around(jenkins)
            .around(new RealJiraSetupRule(jenkins))
            .around(
            new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    jira = new RealJiraRunner(jenkins)
                }
            })

    JiraRunner jira

    def 'Should trigger a build when a comment is added'() {
        given:
        def issueKey = jira.createIssue()
        jenkins.createJiraCommentTriggeredProject('job')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    def 'Should trigger a build when an issue is updated'() {
        given:
        def issueKey = jira.createIssue('Original description')
        jenkins.createJiraChangelogTriggeredProject('job')

        when:
        jira.updateDescription(issueKey, 'New description')

        then:
        jenkins.buildShouldBeScheduled('job')
    }
}
