package com.ceilfors.jenkins.plugins.jirabuilder

import org.jvnet.hudson.test.JenkinsRule

import java.util.concurrent.TimeUnit

/**
 * @author ceilfors
 */
class JenkinsRunner extends JenkinsRule {

    def buildShouldBeScheduled(String jobName) {
        def result = jiraBuilderAction.getLastScheduledBuild(15, TimeUnit.SECONDS)
        if (!result) {
            throw new IllegalStateException("No build is scheduled at all")
        } else {
            println result.get()
            return true
        }
    }

    private JiraWebHook getJiraBuilderAction() {
        instance.getActions().find { it instanceof JiraWebHook } as JiraWebHook
    }

    String getWebHookUrl() {
        return "${getURL().toString()}${jiraBuilderAction.urlName}/"
                .replace("localhost", "10.0.2.2") // vagrant
    }
}

