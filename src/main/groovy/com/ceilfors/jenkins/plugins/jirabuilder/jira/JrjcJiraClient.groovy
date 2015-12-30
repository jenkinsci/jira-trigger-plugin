package com.ceilfors.jenkins.plugins.jirabuilder.jira
import com.atlassian.httpclient.api.HttpClient
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhook
import groovy.json.JsonSlurper

import javax.ws.rs.core.UriBuilder
/**
 * @author ceilfors
 */
class JrjcJiraClient implements Jira {

    private JbRestClient jiraRestClient
    private HttpClient httpClient
    private URI serverUri

    private static final WEBHOOK_NAME = "Jenkins JIRA Builder"

    public JrjcJiraClient() {
        serverUri = "http://localhost:2990/jira".toURI()
        httpClient = new AsynchronousHttpClientFactory()
                .createClient(serverUri, new BasicHttpAuthenticationHandler("admin", "admin"));
        jiraRestClient = new JbRestClient(serverUri, httpClient)
    }

    @Override
    String createIssue() {
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder("TEST", 10000L, "task summary")
        jiraRestClient.issueClient.createIssue(issueInputBuilder.build()).claim().key
    }

    @Override
    String createIssue(String description) {
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder("TEST", 10000L, "task summary")
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
        jiraRestClient.webhookRestClient.registerWebhook(new WebhookInput(
                url: "$url?issue_key=\${issue.key}",
                name: WEBHOOK_NAME,
                events: [JiraWebhook.WEBHOOK_EVENT],
        )).claim()
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
    Map getIssueMap(String issueKey) {
        final URI uri = UriBuilder.fromUri(serverUri)
                .path("/rest/api/latest")
                .path("issue")
                .path(issueKey)
                .build();
        return new JsonSlurper().parseText(httpClient.newRequest(uri).setAccept("application/json").get().claim().entity) as Map
    }

    @Override
    boolean validateIssueKey(String issueKey, String jqlFilter) {
        def searchResult = jiraRestClient.searchClient.searchJql("issueKey=$issueKey and ($jqlFilter)").claim()
        searchResult.total != 0
    }
}
