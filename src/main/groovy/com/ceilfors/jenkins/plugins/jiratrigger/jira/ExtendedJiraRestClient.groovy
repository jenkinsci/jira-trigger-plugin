package com.ceilfors.jenkins.plugins.jiratrigger.jira
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient

import javax.ws.rs.core.UriBuilder

/**
 * Add missing functionality on top of the built-in JiraRestClient.
 *
 * @author ceilfors
 */
class ExtendedJiraRestClient extends AsynchronousJiraRestClient {

    private WebhookRestClient webhookRestClient

    ExtendedJiraRestClient(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient)
        this.webhookRestClient = new AsynchronousWebhookRestClient(UriBuilder.fromUri(serverUri).path("/rest/webhooks/latest").build(), httpClient)
    }

    WebhookRestClient getWebhookRestClient() {
        return webhookRestClient
    }
}
