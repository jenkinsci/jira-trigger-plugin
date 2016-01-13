package com.ceilfors.jenkins.plugins.jirabuilder.webhook

import groovy.json.JsonSlurper
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.UnprotectedRootAction
import org.codehaus.jettison.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.interceptor.RequirePOST

import javax.inject.Inject

/**
 * The HTTP endpoint that receives JIRA Webhook.
 *
 * @author ceilfors
 */
@Log
@Extension
class JiraWebhook implements UnprotectedRootAction {

    public static final URL_NAME = "jira-builder"
    public static final PRIMARY_WEBHOOK_EVENT = "comment_created" // JIRA 7.1+
    public static final SECONDARY_WEBHOOK_EVENT = "jira:issue_updated" // Older JIRA
    private JiraWebhookListener jiraWebhookListener

    JiraWebhook() {
    }

    @Inject
    void setJiraWebhookListener(JiraWebhookListener jiraWebhookListener) {
        this.jiraWebhookListener = jiraWebhookListener
    }

    @Override
    String getIconFileName() {
        return null
    }

    @Override
    String getDisplayName() {
        return "JIRA Builder"
    }

    @Override
    String getUrlName() {
        return URL_NAME
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @RequirePOST
    public void doIndex(StaplerRequest request) {
        processEvent(request, getRequestBody(request))
    }

    public void processEvent(StaplerRequest request, String webhookEvent) {
        Map webhookEventMap = new JsonSlurper().parseText(webhookEvent) as Map
        String eventType = webhookEventMap['webhookEvent']
        if (isCommentEvent(webhookEventMap)) {
            log.fine("Received valid Webhook callback. Event type: ${eventType}")
            WebhookCommentEvent commentEvent = new WebhookCommentEventJsonParser().parse(new JSONObject(webhookEvent))
            commentEvent.userId = request.getParameter("user_id")
            commentEvent.userKey = request.getParameter("user_key")
            jiraWebhookListener.commentCreated(commentEvent)
        } else {
            log.warning("Received Webhook callback with an invalid event type or a body without comment. " +
                    "Event type: ${eventType}. Event body contains: ${webhookEventMap.keySet()}.")
        }
    }

    private boolean isCommentEvent(Map webhookEventMap) {
        String eventType = webhookEventMap["webhookEvent"]
        if (eventType == PRIMARY_WEBHOOK_EVENT) {
            return true
        } else return eventType == SECONDARY_WEBHOOK_EVENT && webhookEventMap["comment"]
    }

    private String getRequestBody(StaplerRequest req) {
        return req.inputStream.text
    }
}
