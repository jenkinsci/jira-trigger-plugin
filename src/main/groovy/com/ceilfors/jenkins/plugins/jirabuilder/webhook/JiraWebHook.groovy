package com.ceilfors.jenkins.plugins.jirabuilder.webhook

import groovy.json.JsonSlurper
import hudson.Extension
import hudson.model.UnprotectedRootAction
import org.kohsuke.stapler.QueryParameter
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.interceptor.RequirePOST

import javax.inject.Inject
/**
 * @author ceilfors
 */
@Extension
class JiraWebHook implements UnprotectedRootAction {

    public static final URLNAME = "jira-builder"
    public static final WEBHOOK_EVENT = "comment_created"
    private JiraWebHookListener jiraWebHookListener

    JiraWebHook() {
    }

    @Inject
    void setJiraWebHookListener(JiraWebHookListener jiraWebHookListener) {
        this.jiraWebHookListener = jiraWebHookListener
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
    public void doIndex(StaplerRequest request, @QueryParameter(required = true) String issue_key) {
        Map webhookEvent = new JsonSlurper().parseText(getRequestBody(request)) as Map
        processEvent(request, issue_key, webhookEvent)
    }

    public void processEvent(StaplerRequest request, String issueKey, Map webhookEvent) {
        if (webhookEvent["webhookEvent"] == WEBHOOK_EVENT) {
            JiraWebHookContext jiraWebHookContext = new JiraWebHookContext(
                    issueKey,
                    webhookEvent.comment as Map
            )
            jiraWebHookContext.userId = request.getParameter("user_id")
            jiraWebHookContext.userKey = request.getParameter("user_key")
            jiraWebHookListener.commentCreated(jiraWebHookContext)
        }
    }

    private String getRequestBody(StaplerRequest req) {
        return req.inputStream.text
    }
}
