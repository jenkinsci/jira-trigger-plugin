package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.Issue

/**
 * @author ceilfors
 */
class WebhookIssueCreatedEvent extends BaseWebhookEvent {

    final Issue issue

    WebhookIssueCreatedEvent(long timestamp, String webhookEventType, Issue issue) {
        super(timestamp, webhookEventType)
        this.issue = issue
    }
}
