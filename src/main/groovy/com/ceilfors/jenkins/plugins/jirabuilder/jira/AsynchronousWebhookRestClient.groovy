package com.ceilfors.jenkins.plugins.jirabuilder.jira

import com.atlassian.httpclient.api.HttpClient
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient
import com.atlassian.jira.rest.client.internal.json.JsonParser
import com.atlassian.util.concurrent.Promise

import javax.ws.rs.core.UriBuilder

/**
 * @author ceilfors
 */
class AsynchronousWebhookRestClient extends AbstractAsynchronousRestClient implements WebhookRestClient {

    private final URI baseUri
    private final JsonParser<?, Iterable<Webhook>> webhooksJsonParser = new WebhooksJsonParser()

    protected AsynchronousWebhookRestClient(final URI baseUri, HttpClient client) {
        super(client)
        this.baseUri = baseUri
    }

    @Override
    public Promise<Void> registerWebhook(WebhookInput webhook) {
        final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("webhook")
        return post(uriBuilder.build(), webhook, new WebhookInputJsonGenerator())
    }

    @Override
    public Promise<Void> unregisterWebhook(URI webhookUri) {
        return delete(webhookUri)
    }

    @Override
    public Promise<Iterable<Webhook>> getWebhooks() {
        final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri).path("webhook")
        return getAndParse(uriBuilder.build(), webhooksJsonParser);
    }
}
