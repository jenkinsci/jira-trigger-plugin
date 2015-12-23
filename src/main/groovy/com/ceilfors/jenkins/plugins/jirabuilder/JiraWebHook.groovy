package com.ceilfors.jenkins.plugins.jirabuilder
import groovy.json.JsonSlurper
import hudson.Extension
import hudson.model.*
import hudson.model.queue.QueueTaskFuture
import jenkins.model.Jenkins
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.Field
import net.rcarz.jiraclient.JiraClient
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse
import org.kohsuke.stapler.interceptor.RequirePOST

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
/**
 * @author ceilfors
 */
@Extension
class JiraWebHook implements UnprotectedRootAction {

    public static final URLNAME = "jira-builder"
    public static final WEBHOOK_EVENT = "comment_created"
    BlockingQueue<QueueTaskFuture<? extends AbstractBuild>> lastScheduledBuild = new ArrayBlockingQueue<>(1)

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
    public void doIndex(StaplerRequest request, StaplerResponse response) {
        def webhookEvent = new JsonSlurper().parseText(getRequestBody(request))
        if (webhookEvent["webhookEvent"] == WEBHOOK_EVENT) {
            lastScheduledBuild.put(Jenkins.instance.getItemByFullName("simplejob", AbstractProject)
                    .scheduleBuild2(0, new JiraBuilderCause(), new ParametersAction(
                    new StringParameterValue("description", getDescription(request))
            )))
        }
    }

    private String getDescription(StaplerRequest request) {
        def issueKey = request.getParameter("issueKey")
        BasicCredentials creds = new BasicCredentials("admin", "admin")
        def jiraClient = new JiraClient("http://localhost:2990/jira", creds)
        return jiraClient.getIssue(issueKey).getField(Field.DESCRIPTION)
    }

    public AbstractBuild getLastScheduledBuild(long timeout, TimeUnit timeUnit) {
        def build = lastScheduledBuild.poll(timeout, timeUnit)
        if (build) {
            return build.get()
        } else {
            throw new RuntimeException("No build has been scheduled")
        }
    }

    private String getRequestBody(StaplerRequest req) {
        return req.inputStream.text
    }
}
