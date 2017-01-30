package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Issue

/**
 * @author ceilfors
 */
class WebhookChangelogEvent extends BaseWebhookEvent {

    final ChangelogGroup changelog
    final Issue issue

    WebhookChangelogEvent(long timestamp, String webhookEventType, Issue issue, ChangelogGroup changelog) {
        super(timestamp, webhookEventType)
        this.issue = issue
        this.changelog = changelog
    }
}
