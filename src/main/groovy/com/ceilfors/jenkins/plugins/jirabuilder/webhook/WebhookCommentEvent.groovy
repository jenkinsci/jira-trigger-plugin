package com.ceilfors.jenkins.plugins.jirabuilder.webhook

import com.atlassian.jira.rest.client.api.domain.Comment

/**
 * @author ceilfors
 */
class WebhookCommentEvent {

    private final Comment comment
    private final long timestamp
    private final String webhookEventType
    private String userId
    private String userKey

    WebhookCommentEvent(long timestamp, String webhookEventType, Comment comment) {
        this.comment = comment
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

    Comment getComment() {
        return comment
    }

    String getWebhookEventType() {
        return webhookEventType
    }

    long getTimestamp() {
        return timestamp
    }
}
