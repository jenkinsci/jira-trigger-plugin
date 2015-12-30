package com.ceilfors.jenkins.plugins.jirabuilder.jira

import com.atlassian.util.concurrent.Promise

/**
 * @author ceilfors
 */
interface WebHookRestClient {


    Promise<Void> registerWebhook(WebhookInput webhook)

    Promise<Void> unregisterWebhook(URI webhookUri)

    Promise<Iterable<Webhook>> getWebhooks()
}