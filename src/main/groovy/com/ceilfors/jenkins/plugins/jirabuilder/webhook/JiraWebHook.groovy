package com.ceilfors.jenkins.plugins.jirabuilder.webhook

import com.ceilfors.jenkins.plugins.jirabuilder.jira.Jira
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
    private Jira jira
    private JiraWebHookListener jiraWebHookListener

    JiraWebHook() {
    }

    @Inject
    void setJiraWebHookListener(JiraWebHookListener jiraWebHookListener) {
        this.jiraWebHookListener = jiraWebHookListener
    }

    @Inject
    void setJira(Jira jira) {
        this.jira = jira
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
    public void doIndex(StaplerRequest request, @QueryParameter(required = true) String issueKey) {
        Map webhookEvent = new JsonSlurper().parseText(getRequestBody(request)) as Map
        processEvent(webhookEvent, issueKey)
    }

    public void processEvent(Map webhookEvent, String issueKey) {
        if (webhookEvent["webhookEvent"] == WEBHOOK_EVENT) {
            def issue = jira.getIssueMap(issueKey)
            jiraWebHookListener.commentCreated(issue, webhookEvent.comment as Map)
        }
    }

    private String getRequestBody(StaplerRequest req) {
        return req.inputStream.text
    }
}
