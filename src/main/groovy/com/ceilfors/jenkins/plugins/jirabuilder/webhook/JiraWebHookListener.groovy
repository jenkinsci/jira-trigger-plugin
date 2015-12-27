package com.ceilfors.jenkins.plugins.jirabuilder.webhook

/**
 * @author ceilfors
 */
interface JiraWebHookListener {

    void commentCreated(Map issue, Map comment)
}
