package com.ceilfors.jenkins.plugins.jiratrigger.webhook
/**
 * @author ceilfors
 */
abstract class AbstractWebhookEvent {

    private final long timestamp
    private final String webhookEventType
    private String userId
    private String userKey

    public AbstractWebhookEvent(long timestamp, String webhookEventType) {
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

    String getWebhookEventType() {
        return webhookEventType
    }

    long getTimestamp() {
        return timestamp
    }
}
