package com.ceilfors.jenkins.plugins.jirabuilder

import hudson.Extension
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.UnprotectedRootAction
import hudson.model.queue.QueueTaskFuture
import jenkins.model.Jenkins
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

    @RequirePOST
    public void doIndex(StaplerRequest request, StaplerResponse response) {
        println getRequestBody(request)
        lastScheduledBuild.put(Jenkins.instance.getItemByFullName("simplejob", AbstractProject).scheduleBuild2(0))
    }

    private String getDescriptionFromCommentEvent(webhookEvent) {
        // parse issue id from "self": "http://localhost:2990/jira/rest/api/2/issue/10003/comment/10000"
        // Hit JIRA REST API to retrieve description
        // Return description
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
