package com.ceilfors.jenkins.plugins.jiratrigger
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder
import com.atlassian.jira.rest.client.api.RestClientException
import com.atlassian.jira.rest.client.api.domain.CimProject
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JrjcJiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.Webhook
import com.ceilfors.jenkins.plugins.jiratrigger.jira.WebhookInput
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhook
import groovy.util.logging.Log
import hudson.model.AbstractProject
import hudson.model.Job
import hudson.model.Queue
import jenkins.model.Jenkins

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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

    private static final WEBHOOK_NAME = "Jenkins JIRA Trigger"

    private Jenkins jenkins

    BlockingQueue<Queue.Item> scheduledItem = new ArrayBlockingQueue<>(1)
    CountDownLatch noBuildLatch = new CountDownLatch(1)

    RealJiraRunner(JenkinsRunner jenkinsRunner, JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration) {
        super(jiraTriggerGlobalConfiguration)
        this.jenkins = jenkinsRunner.jenkins

        jenkinsRunner.jiraTriggerExecutor.addJiraTriggerListener(new JiraTriggerListener() {

            @Override
            void buildScheduled(Comment comment, Collection<? extends AbstractProject> projects) {
                scheduledItem.offer(jenkins.queue.getItems().last(), 5, TimeUnit.SECONDS)
            }

            @Override
            void buildNotScheduled(Comment comment) {
                noBuildLatch.countDown()
            }
        })
    }

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
    void shouldBeNotifiedWithComment(String issueKey, String jobName) {
        Queue.Item scheduledItem = this.scheduledItem.poll(5, TimeUnit.SECONDS)
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
