package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
/**
 * @author ceilfors
 */
class WebhookChangelogEvent extends AbstractWebhookEvent {

    private final ChangelogGroup changelog

    WebhookChangelogEvent(long timestamp, String webhookEventType, ChangelogGroup changelog) {
        super(timestamp, webhookEventType)
        this.changelog = changelog
    }

    ChangelogGroup getChangelog() {
        return changelog
    }
}
