package com.ceilfors.jenkins.plugins.jiratrigger.integration

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
/**
 * @author ceilfors
 */
class FakeJiraRunner implements JiraRunner {

    public static final String CUSTOM_FIELD_NAME = 'My Customer Custom Field'

    private final RESTClient restClient
    private int id = 1
    private final Map<String, FakeIssue> issueMap = [:]

    FakeJiraRunner(JenkinsRunner jenkinsRunner) {
        restClient = createRestClient(jenkinsRunner.webhookUrl)
    }

    @Override
    String createIssue() {
        createIssue('')
    }

    @Override
    String createIssue(String description) {
        String issueKey = "TEST-$id"
        id++
        issueMap[issueKey] = new FakeIssue(issueKey: issueKey, description: description)
        issueKey
    }

    @Override
    void updateDescription(String issueKey, String description) {
        issueMap[issueKey].description = description
        Map body = createPostBody('updateDescription', issueKey)
        body.changelog.items[0].toString = description
        restClient.post(body: body)
    }

    @Override
    void updateStatus(String issueKey, String status) {
        Map body = createPostBody('updateStatus', issueKey)
        body.changelog.items[0].toString = status
        restClient.post(body: body)
    }

    @Override
    void updateCustomField(String issueKey, String fieldName, String value) {
        Map body = createPostBody('updateCustomField', issueKey)
        body.changelog.items[0].field = fieldName
        body.changelog.items[0].toString = value
        restClient.post(body: body)
    }

    @Override
    void addComment(String issueKey, String comment) {
        Map body = createPostBody('addComment', issueKey)
        body.comment.body = comment
        restClient.post(body: body)
    }

    @Override
    boolean validateIssueKey(String issueKey, String jqlFilter) {
        true
    }

    private RESTClient createRestClient(String jenkinsUrl) {
        new RESTClient(jenkinsUrl, ContentType.JSON)
    }

    protected Map createPostBody(String method, String issueKey) {
        def slurper = new JsonSlurper()
        Map body = slurper.parse(new FileReader(new File(this.class.getResource("${method}.json").toURI()))) as Map
        body.issue.key = issueKey
        body.issue.fields.description = issueMap[issueKey].description
        body
    }

    protected RESTClient getRestClient() {
        restClient
    }

    protected static class FakeIssue {
        String issueKey
        String description
    }
}
