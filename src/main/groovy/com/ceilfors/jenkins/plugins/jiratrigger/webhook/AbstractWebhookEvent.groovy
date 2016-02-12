package com.ceilfors.jenkins.plugins.jiratrigger.webhook
/**
 * @author ceilfors
 */
abstract class AbstractWebhookEvent {

    final long timestamp
    final String webhookEventType
    String userId
    String userKey

    public AbstractWebhookEvent(long timestamp, String webhookEventType) {
        this.timestamp = timestamp
        this.webhookEventType = webhookEventType
    }
}
