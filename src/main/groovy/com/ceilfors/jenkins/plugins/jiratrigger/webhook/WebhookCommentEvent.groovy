package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.api.domain.Comment

/**
 * @author ceilfors
 */
class WebhookCommentEvent extends AbstractWebhookEvent {

    private final Comment comment

    WebhookCommentEvent(long timestamp, String webhookEventType, Comment comment) {
        super(timestamp, webhookEventType)
        this.comment = comment
    }

    Comment getComment() {
        return comment
    }
}
