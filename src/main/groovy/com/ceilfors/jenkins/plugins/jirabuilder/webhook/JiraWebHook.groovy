package com.ceilfors.jenkins.plugins.jirabuilder.webhook

import groovy.json.JsonSlurper
import hudson.Extension
import hudson.model.UnprotectedRootAction
import org.codehaus.jettison.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.interceptor.RequirePOST

import javax.inject.Inject

/**
 * @author ceilfors
 */
@Extension
class JiraWebhook implements UnprotectedRootAction {

    public static final URLNAME = "jira-builder"
    public static final WEBHOOK_EVENT = "comment_created"
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
        return URLNAME
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @RequirePOST
    public void doIndex(StaplerRequest request) {
        processEvent(request, getRequestBody(request))
    }

    public void processEvent(StaplerRequest request, String webhookEvent) {
        Map webhookEventMap = new JsonSlurper().parseText(webhookEvent) as Map
        if (webhookEventMap["webhookEvent"] == WEBHOOK_EVENT) {
            WebhookCommentEvent commentEvent = new WebhookCommentEventJsonParser().parse(new JSONObject(webhookEvent))
            commentEvent.userId = request.getParameter("user_id")
            commentEvent.userKey = request.getParameter("user_key")
            jiraWebhookListener.commentCreated(commentEvent)
        }
    }

    private String getRequestBody(StaplerRequest req) {
        return req.inputStream.text
    }
}
