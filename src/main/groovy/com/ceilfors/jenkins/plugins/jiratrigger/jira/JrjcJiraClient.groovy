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
/**
 * @author ceilfors
 */
@Singleton
@Log
class JrjcJiraClient implements JiraClient {

    JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration

    @Inject
    public JrjcJiraClient(JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration) {
        this.jiraTriggerGlobalConfiguration = jiraTriggerGlobalConfiguration
    }

    protected URI getServerUri() {
        return jiraTriggerGlobalConfiguration.rootUrl.toURI()
    }

    protected DisposableHttpClient getHttpClient() {
        return new AsynchronousHttpClientFactory()
                .createClient(serverUri,
                new BasicHttpAuthenticationHandler(jiraTriggerGlobalConfiguration.username, jiraTriggerGlobalConfiguration.password.plainText));
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
                .build();
        return new JsonSlurper().parseText(httpClient.newRequest(uri).setAccept("application/json").get().claim().entity) as Map
    }

    @Override
    boolean validateIssueId(String issueId, String jqlFilter) {
        def searchResult = jiraRestClient.searchClient.searchJql("id=$issueId and ($jqlFilter)").claim()
        searchResult.total != 0
    }

    @Override
    void addComment(String issueKey, String comment) {
        def issue = jiraRestClient.issueClient.getIssue(issueKey).claim()
        jiraRestClient.issueClient.addComment(issue.commentsUri, Comment.valueOf(comment)).claim()
    }
}
