package com.ceilfors.jenkins.plugins.jiratrigger.jira

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.google.inject.Singleton
import groovy.json.JsonSlurper
import groovy.util.logging.Log

import javax.inject.Inject
import javax.ws.rs.core.UriBuilder
import java.util.concurrent.TimeUnit

/**
 * @author ceilfors
 */
@Singleton
@Log
class JrjcJiraClient implements JiraClient {

    long timeout = 10
    TimeUnit timeoutUnit = TimeUnit.SECONDS

    JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration

    @Inject
    public JrjcJiraClient(JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration) {
        this.jiraTriggerGlobalConfiguration = jiraTriggerGlobalConfiguration
    }

    protected URI getServerUri() {
        jiraTriggerGlobalConfiguration.validateConfiguration()
        return jiraTriggerGlobalConfiguration.jiraRootUrl.toURI()
    }

    protected DisposableHttpClient getHttpClient() {
        return new AsynchronousHttpClientFactory()
                .createClient(serverUri,
                new BasicHttpAuthenticationHandler(jiraTriggerGlobalConfiguration.jiraUsername, jiraTriggerGlobalConfiguration.jiraPassword.plainText));
    }

    protected JbRestClient getJiraRestClient() {
        return new JbRestClient(serverUri, httpClient)
    }

    @Override
    Map getIssueMap(String issueKeyOrId) {
        final URI uri = UriBuilder.fromUri(serverUri)
                .path("/rest/api/latest")
                .path("issue")
                .path(issueKeyOrId)
                .build()
        return new JsonSlurper().parseText(httpClient.newRequest(uri).setAccept("application/json").get().get(timeout, timeoutUnit).entity) as Map
    }

    @Override
    boolean validateIssueId(Long issueId, String jqlFilter) {
        def searchResult = jiraRestClient.searchClient.searchJql("id=$issueId and ($jqlFilter)").get(timeout, timeoutUnit)
        searchResult.total != 0
    }

    @Override
    void addComment(String issueKey, String comment) {
        def issue = jiraRestClient.issueClient.getIssue(issueKey).get(timeout, timeoutUnit)
        jiraRestClient.issueClient.addComment(issue.commentsUri, Comment.valueOf(comment)).get(timeout, timeoutUnit)
    }
}
