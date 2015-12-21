package com.ceilfors.jenkins.plugins.jirabuilder

import org.jvnet.hudson.test.JenkinsRule

/**
 * @author ceilfors
 */
class JenkinsRunner extends JenkinsRule {

    def buildShouldBeScheduled(String jobName) {
        return false
    }
}
