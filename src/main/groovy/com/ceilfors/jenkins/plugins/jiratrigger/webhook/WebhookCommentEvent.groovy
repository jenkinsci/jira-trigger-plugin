package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue

/**
 * @author ceilfors
 */
class WebhookCommentEvent extends AbstractWebhookEvent {

    private final Comment comment
    private final Issue issue

    WebhookCommentEvent(long timestamp, String webhookEventType, Issue issue, Comment comment) {
        super(timestamp, webhookEventType)
        this.comment = comment
        this.issue = issue
    }

    Comment getComment() {
        return comment
    }

    Issue getIssue() {
        return issue
    }
}
