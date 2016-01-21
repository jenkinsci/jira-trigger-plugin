package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
/**
 * @author ceilfors
 */
class WebhookChangelogEvent {

    private final ChangelogGroup changelog
    private final long timestamp
    private final String webhookEventType
    private String userId
    private String userKey

    WebhookChangelogEvent(long timestamp, String webhookEventType, ChangelogGroup changelog) {
        this.changelog = changelog
        this.timestamp = timestamp
        this.webhookEventType = webhookEventType
    }

    void setUserId(String userId) {
        this.userId = userId
    }

    String getUserId() {
        return userId
    }

    void setUserKey(String userKey) {
        this.userKey = userKey
    }

    String getUserKey() {
        return userKey
    }

    ChangelogGroup getChangelog() {
        return changelog
    }

    String getWebhookEventType() {
        return webhookEventType
    }

    long getTimestamp() {
        return timestamp
    }
}
