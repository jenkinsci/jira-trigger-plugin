package com.ceilfors.jenkins.plugins.jirabuilder.jira
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.domain.CimProject
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient
import com.ceilfors.jenkins.plugins.jirabuilder.JiraBuilderGlobalConfiguration
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhook
import com.google.inject.Singleton
import groovy.json.JsonSlurper
import groovy.util.logging.Log

import javax.inject.Inject
import javax.ws.rs.core.UriBuilder
/**
 * @author ceilfors
 */
@Singleton
@Log
class JrjcJiraClient implements JiraClient {

    private static final WEBHOOK_NAME = "Jenkins JIRA Builder"
    JiraBuilderGlobalConfiguration jiraBuilderGlobalConfiguration

    @Inject
    public JrjcJiraClient(JiraBuilderGlobalConfiguration jiraBuilderGlobalConfiguration) {
        this.jiraBuilderGlobalConfiguration = jiraBuilderGlobalConfiguration
    }

    private URI getServerUri() {
        return jiraBuilderGlobalConfiguration.rootUrl.toURI()
    }

    private DisposableHttpClient getHttpClient() {
        return new AsynchronousHttpClientFactory()
                .createClient(serverUri,
                new BasicHttpAuthenticationHandler(jiraBuilderGlobalConfiguration.username, jiraBuilderGlobalConfiguration.password.plainText));
    }

    private JbRestClient getJiraRestClient() {
        return new JbRestClient(serverUri, httpClient)
    }

    @Override
    String createIssue() {
        Iterable<CimProject> metadata = jiraRestClient.issueClient.getCreateIssueMetadata(new GetCreateIssueMetadataOptionsBuilder().withProjectKeys("TEST").withIssueTypeNames("Task").build()).claim()
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder("TEST", metadata[0].issueTypes[0].id, "task summary")
        jiraRestClient.issueClient.createIssue(issueInputBuilder.build()).claim().key
    }

    @Override
    String createIssue(String description) {
        Iterable<CimProject> metadata = jiraRestClient.issueClient.getCreateIssueMetadata(new GetCreateIssueMetadataOptionsBuilder().withProjectKeys("TEST").withIssueTypeNames("Task").build()).claim()
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder("TEST", metadata[0].issueTypes[0].id, "task summary")
        issueInputBuilder.description = description
        jiraRestClient.issueClient.createIssue(issueInputBuilder.build()).claim().key
    }

    @Override
    void addComment(String issueKey, String comment) {
        def issue = jiraRestClient.issueClient.getIssue(issueKey).claim()

        jiraRestClient.issueClient.addComment(issue.commentsUri, Comment.valueOf(comment)).claim()
    }

    @Override
    void registerWebhook(String url) {
        try {
            jiraRestClient.webhookRestClient.registerWebhook(new WebhookInput(
                    url: "$url",
                    name: WEBHOOK_NAME,
                    events: [JiraWebhook.PRIMARY_WEBHOOK_EVENT],
            )).claim()
        } catch (RestClientException e) {
            if (e.statusCode.present && e.statusCode.get() == 400) {
                // Might be caused by comment_created event not supported yet. Response body is not available in the
                // exception and it requires complex overriding in AsynchronousWebhookRestClient to read the response.
                log.warning("${JiraWebhook.PRIMARY_WEBHOOK_EVENT} JIRA Webhook event is not available, using ${JiraWebhook.SECONDARY_WEBHOOK_EVENT}")
                jiraRestClient.webhookRestClient.registerWebhook(new WebhookInput(
                        url: "$url",
                        name: WEBHOOK_NAME,
                        events: [JiraWebhook.SECONDARY_WEBHOOK_EVENT],
                )).claim()
            } else {
                throw e
            }
        }
    }

    @Override
    def deleteAllWebhooks() {
        Iterable<Webhook> webhooks = jiraRestClient.webhookRestClient.getWebhooks().claim()
        def webhook = webhooks.find { it.name == WEBHOOK_NAME } as Webhook
        if (webhook) {
            jiraRestClient.webhookRestClient.unregisterWebhook(webhook.selfUri).claim()
        }
    }

    @Override
    Map getIssueMap(String issueKeyOrId) {
        final URI uri = UriBuilder.fromUri(serverUri)
                .path("/rest/api/latest")
                .path("issue")
                .path(issueKeyOrId)
                .build();
        return new JsonSlurper().parseText(httpClient.newRequest(uri).setAccept("application/json").get().claim().entity) as Map
    }

    @Override
    boolean validateIssueId(String issueId, String jqlFilter) {
        def searchResult = jiraRestClient.searchClient.searchJql("id=$issueId and ($jqlFilter)").claim()
        searchResult.total != 0
    }
}
