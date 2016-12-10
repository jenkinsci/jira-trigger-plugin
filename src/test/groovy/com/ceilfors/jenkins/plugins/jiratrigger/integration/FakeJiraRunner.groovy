package com.ceilfors.jenkins.plugins.jiratrigger.integration

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

/**
 * @author ceilfors
 */
class FakeJiraRunner implements JiraRunner {

    private RESTClient restClient

    FakeJiraRunner(String webhookUrl) {
        restClient = createRestClient(webhookUrl)
    }

    @Override
    String createIssue() {
        return 'TEST-1'
    }

    @Override
    String createIssue(String description) {
        return 'TEST-1'
    }

    @Override
    void updateDescription(String issueKey, String description) {
        Map body = new JsonSlurper().parse(new FileReader(new File(this.class.getResource('updateDescription.json').toURI()))) as Map
        body.issue.key = issueKey
        body.changelog.items[0].toString = description
        restClient.post(body: body)
    }

    @Override
    void updateStatus(String issueKey, String status) {
        Map body = new JsonSlurper().parse(new FileReader(new File(this.class.getResource('updateStatus.json').toURI()))) as Map
        body.issue.key = issueKey
        body.changelog.items[0].toString = status
        restClient.post(body: body)
    }

    @Override
    void shouldBeNotifiedWithComment(String issueKey, String jobName) {

    }

    @Override
    void updateCustomField(String issueKey, String fieldName, String value) {
        Map body = new JsonSlurper().parse(new FileReader(new File(this.class.getResource('updateCustomField.json').toURI()))) as Map
        body.issue.key = issueKey
        body.changelog.items[0].field = fieldName
        body.changelog.items[0].toString = value
        restClient.post(body: body)
    }

    @Override
    void addComment(String issueKey, String comment) {
        Map body = new JsonSlurper().parse(new FileReader(new File(this.class.getResource('addComment.json').toURI()))) as Map
        body.issue.key = issueKey
        body.comment.body = comment
        restClient.post(body: body)
    }

    @Override
    boolean validateIssueKey(String issueKey, String jqlFilter) {
        return false
    }

    private RESTClient createRestClient(String jenkinsUrl) {
        new RESTClient(jenkinsUrl, ContentType.JSON)
    }
}
