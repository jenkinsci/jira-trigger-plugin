package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.integration.JenkinsRunner
import com.ceilfors.jenkins.plugins.jiratrigger.integration.JulLogLevelRule
import hudson.model.AbstractBuild
import hudson.model.Queue
import org.junit.Rule
import org.junit.rules.RuleChain
import spock.lang.Specification

import java.util.logging.Level

/**
 * @author ceilfors
 */
class JiraTriggerIntegrationTest extends Specification {

    JenkinsRunner jenkins = new JenkinsRunner()

    @Rule
    RuleChain ruleChain = RuleChain
            .outerRule(new JulLogLevelRule(Level.FINEST))
            .around(jenkins)


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

    // Add jenkins.configRoundTrip for testing
}
