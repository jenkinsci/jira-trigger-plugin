package com.ceilfors.jenkins.plugins.jiratrigger.jira

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.SearchResult
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.google.inject.Singleton
import groovy.util.logging.Log
import hudson.Extension
import hudson.XmlFile
import hudson.model.Saveable
import hudson.model.listeners.SaveableListener

import javax.inject.Inject
import java.util.concurrent.TimeUnit

/**
 * @author ceilfors
 */
@Singleton
@Log
class JrjcJiraClient implements JiraClient {

    long timeout = 50
    TimeUnit timeoutUnit = TimeUnit.SECONDS

    JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration

    private ExtendedJiraRestClient extendedJiraRestClient

    @Inject
    JrjcJiraClient(JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration) {
        this.jiraTriggerGlobalConfiguration = jiraTriggerGlobalConfiguration
    }

    protected URI getServerUri() {
        jiraTriggerGlobalConfiguration.validateConfiguration()
        jiraTriggerGlobalConfiguration.jiraRootUrl.toURI()
    }

    protected DisposableHttpClient getHttpClient() {
        String username = jiraTriggerGlobalConfiguration.jiraUsername
        String password = jiraTriggerGlobalConfiguration.jiraPassword.plainText
        new AsynchronousHttpClientFactory()
                .createClient(serverUri,
                new BasicHttpAuthenticationHandler(username, password))
    }

    ExtendedJiraRestClient getJiraRestClient() {
        if (extendedJiraRestClient == null) {
            this.extendedJiraRestClient = new ExtendedJiraRestClient(serverUri, httpClient)
        }
        extendedJiraRestClient
    }

    @Override
    boolean validateIssueKey(String issueKey, String jqlFilter) {
        String jql = "key=$issueKey and ($jqlFilter)"
        SearchResult searchResult = jiraRestClient.searchClient.searchJql(jql).get(timeout, timeoutUnit)
        searchResult.total != 0
    }

    @Override
    void addComment(String issueKey, String comment) {
        Issue issue = jiraRestClient.issueClient.getIssue(issueKey).get(timeout, timeoutUnit)
        jiraRestClient.issueClient.addComment(issue.commentsUri, Comment.valueOf(comment)).get(timeout, timeoutUnit)
    }

    @Extension
    static class ResourceCleaner extends SaveableListener {

        @Inject
        JrjcJiraClient jiraClient

        @SuppressWarnings('Instanceof')
        @Override
        void onChange(Saveable o, XmlFile file) {
            if (o instanceof JiraTriggerGlobalConfiguration) {
                if (jiraClient.extendedJiraRestClient != null) {
                    jiraClient.extendedJiraRestClient.close()
                    jiraClient.extendedJiraRestClient = null
                }
            }
        }
    }
}
