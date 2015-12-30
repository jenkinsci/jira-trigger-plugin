package com.ceilfors.jenkins.plugins.jirabuilder.jira
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient

import javax.ws.rs.core.UriBuilder
/**
 * @author ceilfors
 */
class JbRestClient extends AsynchronousJiraRestClient {

    private WebHookRestClient webHookRestClient

    JbRestClient(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient)
        this.webHookRestClient = new AsynchronousWebhookRestClient(UriBuilder.fromUri(serverUri).path("/rest/webhooks/latest").build(), httpClient)
    }

    WebHookRestClient getWebHookRestClient() {
        return webHookRestClient
    }
}
