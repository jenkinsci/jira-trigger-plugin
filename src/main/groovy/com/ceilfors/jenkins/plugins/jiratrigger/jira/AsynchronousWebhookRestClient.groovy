package com.ceilfors.jenkins.plugins.jiratrigger.jira

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
    Promise<Void> registerWebhook(WebhookInput webhook) {
        post(uriBuilder.build(), webhook, new WebhookInputJsonGenerator())
    }

    @Override
    Promise<Void> unregisterWebhook(URI webhookUri) {
        delete(webhookUri)
    }

    @Override
    Promise<Iterable<Webhook>> getWebhooks() {
        getAndParse(uriBuilder.build(), webhooksJsonParser)
    }

    private UriBuilder getUriBuilder() {
        UriBuilder.fromUri(baseUri).path('webhook')
    }
}
