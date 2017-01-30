package com.ceilfors.jenkins.plugins.jiratrigger.webhook

/**
 * @author ceilfors
 */
class BaseWebhookEvent {

    final long timestamp
    final String webhookEventType
    String userId
    String userKey

    protected BaseWebhookEvent(long timestamp, String webhookEventType) {
        this.timestamp = timestamp
        this.webhookEventType = webhookEventType
    }
}
