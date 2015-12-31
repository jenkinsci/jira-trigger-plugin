package com.ceilfors.jenkins.plugins.jirabuilder

/**
 * @author ceilfors
 */
interface JiraBuilderListener {
    def buildScheduled(String issueKey, String commentBody, String jobName)
    def buildNotScheduled(String issueKey, String commentBody)
}
