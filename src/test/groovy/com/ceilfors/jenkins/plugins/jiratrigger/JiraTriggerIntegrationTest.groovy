package com.ceilfors.jenkins.plugins.jiratrigger

import org.junit.Rule
import spock.lang.Specification

/**
 * @author ceilfors
 */
class JiraTriggerIntegrationTest extends Specification {

    @Rule
    JenkinsRunner jenkins = new JenkinsRunner()

    def 'Comment pattern by default must not be empty'() {
        when:
        def project = jenkins.createJiraCommentTriggeredProject("job")

        then:
        project.commentPatternShouldNotBeEmpty()
    }

    // Add jenkins.configRoundTrip for testing
}
