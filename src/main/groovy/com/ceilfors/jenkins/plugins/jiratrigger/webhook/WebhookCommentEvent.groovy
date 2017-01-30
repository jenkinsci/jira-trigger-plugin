package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue

/**
 * @author ceilfors
 */
class WebhookCommentEvent extends BaseWebhookEvent {

    final Comment comment
    final Issue issue

    WebhookCommentEvent(long timestamp, String webhookEventType, Issue issue, Comment comment) {
        super(timestamp, webhookEventType)
        this.comment = comment
        this.issue = issue
    }
}
