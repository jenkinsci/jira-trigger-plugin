package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder
import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.domain.CimProject
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.Transition
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JrjcJiraClient
import groovy.util.logging.Log
import hudson.model.Job
import hudson.model.Queue
import jenkins.model.GlobalConfiguration

import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.not
import static org.hamcrest.Matchers.nullValue
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
@Log
class RealJiraRunner extends JrjcJiraClient implements JiraRunner {

    JenkinsBlockingQueue jenkinsQueue
    JenkinsRunner jenkinsRunner

    RealJiraRunner(JenkinsRunner jenkinsRunner) {
        super(GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration))
        this.jenkinsQueue = new JenkinsBlockingQueue(jenkinsRunner.instance)
        this.jenkinsRunner = jenkinsRunner
    }

    String createIssue() {
        createIssue('')
    }

    String createIssue(String description) {
        Long issueTypeId = getIssueTypeId('TEST', 'Task')
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder('TEST', issueTypeId, 'task summary')
        if (description) {
            issueInputBuilder.description = description
        }
        jiraRestClient.issueClient.createIssue(issueInputBuilder.build()).claim().key
    }

    @Override
    void updateDescription(String issueKey, String description) {
        def issue = new IssueInputBuilder().setDescription(description).build()
        jiraRestClient.issueClient.updateIssue(issueKey, issue).get(timeout, timeoutUnit)
    }

    @Override
    void updateStatus(String issueKey, String status) {
        def issue = jiraRestClient.issueClient.getIssue(issueKey, [IssueRestClient.Expandos.TRANSITIONS])
                .get(timeout, timeoutUnit)
        jiraRestClient.issueClient.transition(issue, new TransitionInput(getTransition(issue, status).id))
                .get(timeout, timeoutUnit)
    }

    private Transition getTransition(Issue issue, String status) {
        Iterable<Transition> transitions = jiraRestClient.issueClient.getTransitions(issue).get(timeout, timeoutUnit)
        String transitionName
        if (status == 'Done') {
            transitionName = 'Done'
        } else if (status == 'In Progress') {
            transitionName = 'Start Progress'
        } else {
            throw new UnsupportedOperationException('Configure this method to support more transition name. ' +
                    "Available transitions: ${transitions*.name}")
        }
        transitions.find { it.name == transitionName } as Transition
    }

    @Override
    void shouldBeNotifiedWithComment(String issueKey, String jobName) {
        Queue.Item scheduledItem = jenkinsQueue.scheduledJobs
        assertThat('Build is not scheduled!', scheduledItem, is(not(nullValue())))

        def issue = jiraRestClient.issueClient.getIssue(issueKey).claim()
        Comment lastComment = issue.getComments().last()
        Job job = jenkinsRunner.instance.getItemByFullName(jobName, Job)
        assertThat("$issueKey was not notified by Jenkins!", lastComment.body, containsString(job.absoluteUrl))
    }

    @Override
    void updateCustomField(String issueKey, String fieldName, String value) {
        String fieldId
        if (fieldName == RealJiraSetupRule.CUSTOM_FIELD_NAME) {
            fieldId = RealJiraSetupRule.customFieldId
        } else {
            throw new UnsupportedOperationException("$fieldName not supported")
        }
        def issue = new IssueInputBuilder().setFieldValue(fieldId, value).build()
        jiraRestClient.issueClient.updateIssue(issueKey, issue).get(timeout, timeoutUnit)
    }

    private Long getIssueTypeId(String project, String issueTypeName) {
        def options = new GetCreateIssueMetadataOptionsBuilder()
                .withProjectKeys(project)
                .withIssueTypeNames(issueTypeName)
                .build()
        Iterable<CimProject> metadata = jiraRestClient.issueClient.getCreateIssueMetadata(options).claim()
        metadata[0].issueTypes[0].id
    }
}
