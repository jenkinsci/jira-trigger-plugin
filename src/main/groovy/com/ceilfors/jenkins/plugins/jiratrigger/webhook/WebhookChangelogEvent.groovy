package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Issue

/**
 * @author ceilfors
 */
class WebhookChangelogEvent extends AbstractWebhookEvent {

    private final ChangelogGroup changelog
    private final Issue issue

    WebhookChangelogEvent(long timestamp, String webhookEventType, Issue issue, ChangelogGroup changelog) {
        super(timestamp, webhookEventType)
        this.issue = issue
        this.changelog = changelog
    }

    ChangelogGroup getChangelog() {
        return changelog
    }

    Issue getIssue() {
        return issue
    }
}
