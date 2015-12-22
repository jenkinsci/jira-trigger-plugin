package com.ceilfors.jenkins.plugins.jirabuilder
import hudson.Extension
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
    BlockingQueue<QueueTaskFuture> lastScheduledBuild = new ArrayBlockingQueue<>(1)

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

    public QueueTaskFuture getLastScheduledBuild(long timeout, TimeUnit timeUnit) {
        return lastScheduledBuild.poll(timeout, timeUnit)
    }

    private String getRequestBody(StaplerRequest req) {
        return req.inputStream.text
    }
}
