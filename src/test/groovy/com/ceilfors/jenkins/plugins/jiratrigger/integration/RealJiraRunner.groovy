package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder
import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.domain.*
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JrjcJiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.Webhook
import com.ceilfors.jenkins.plugins.jiratrigger.jira.WebhookInput
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhook
import groovy.util.logging.Log
import hudson.model.Job
import hudson.model.Queue
import jenkins.model.Jenkins

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat

/**
 * @author ceilfors
 */
@Log
class RealJiraRunner extends JrjcJiraClient implements JiraRunner {

    private static final WEBHOOK_NAME = "Jenkins JIRA Trigger"
    private Jenkins jenkins
    JenkinsBlockingQueue jenkinsQueue

    RealJiraRunner(JenkinsRunner jenkinsRunner, JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration) {
        super(jiraTriggerGlobalConfiguration)
        this.jenkins = jenkinsRunner.jenkins
        jenkinsQueue = new JenkinsBlockingQueue(jenkins)
    }

    void registerWebhook(String url) {
        jiraRestClient.webhookRestClient.registerWebhook(new WebhookInput(
                url: "$url",
                name: WEBHOOK_NAME,
                events: [JiraWebhook.WEBHOOK_EVENT],
        )).claim()
    }

    void deleteAllWebhooks() {
        Iterable<Webhook> webhooks = jiraRestClient.webhookRestClient.getWebhooks().claim()
        // TODO: Should find by base URL
        // TODO: Expose only 1 method: re-registerWebhook() when moving functionality to Jenkins
        def webhook = webhooks.find { it.name == WEBHOOK_NAME } as Webhook
        if (webhook) {
            jiraRestClient.webhookRestClient.unregisterWebhook(webhook.selfUri).claim()
        }
    }

    String createIssue() {
        createIssue("")
    }

    String createIssue(String description) {
        Long issueTypeId = getIssueTypeId("TEST", "Task")
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder("TEST", issueTypeId, "task summary")
        if (description) {
            issueInputBuilder.description = description
        }
        jiraRestClient.issueClient.createIssue(issueInputBuilder.build()).claim().key
    }

    @Override
    void updateDescription(String issueKey, String description) {
        jiraRestClient.issueClient.updateIssue(issueKey, new IssueInputBuilder().setDescription(description).build()).get(timeout, timeoutUnit)
    }

    @Override
    void updateStatus(String issueKey, String status) {
        def issue = jiraRestClient.issueClient.getIssue(issueKey, [IssueRestClient.Expandos.TRANSITIONS]).get(timeout, timeoutUnit)
        jiraRestClient.issueClient.transition(issue, new TransitionInput(getTransition(issue, status).id)).get(timeout, timeoutUnit)
    }

    private Transition getTransition(Issue issue, String status) {
        Iterable<Transition> transitions = jiraRestClient.issueClient.getTransitions(issue).get(timeout, timeoutUnit)
        String transitionName
        if (status == 'Done') {
            transitionName = 'Done'
        } else if (status == 'In Progress') {
            transitionName = 'Start Progress'
        } else {
            throw new UnsupportedOperationException("Configure this method to support more transition name. Available transitions: ${transitions*.name}")
        }
        return transitions.find { it.name == transitionName } as Transition
    }

    @Override
    void shouldBeNotifiedWithComment(String issueKey, String jobName) {
        Queue.Item scheduledItem = jenkinsQueue.scheduledItem
        assertThat("Build is not scheduled!", scheduledItem, is(not(nullValue())))

        def issue = jiraRestClient.issueClient.getIssue(issueKey).claim()
        Comment lastComment = issue.getComments().last()
        Job job = jenkins.getItemByFullName(jobName, Job)
        assertThat("$issueKey was not notified by Jenkins!", lastComment.body, containsString(job.absoluteUrl))
    }

    private Long getIssueTypeId(String project, String issueTypeName) {
        Iterable<CimProject> metadata = jiraRestClient.issueClient.getCreateIssueMetadata(new GetCreateIssueMetadataOptionsBuilder().withProjectKeys(project).withIssueTypeNames(issueTypeName).build()).claim()
        return metadata[0].issueTypes[0].id
    }
}
