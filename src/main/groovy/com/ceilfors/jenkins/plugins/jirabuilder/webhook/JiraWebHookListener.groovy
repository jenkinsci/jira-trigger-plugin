package com.ceilfors.jenkins.plugins.jirabuilder.webhook

/**
 * @author ceilfors
 */
interface JiraWebhookListener {

    void commentCreated(JiraWebhookContext jiraWebhookContext)
}
