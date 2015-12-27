package com.ceilfors.jenkins.plugins.jirabuilder

import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebHookListener
import com.google.inject.Singleton
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.ParametersAction
import hudson.model.StringParameterValue
import hudson.model.queue.QueueTaskFuture
import jenkins.model.Jenkins

import javax.inject.Inject
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * @author ceilfors
 */
@Singleton
class JiraBuilder implements JiraWebHookListener {

    private static final Logger LOGGER = Logger.getLogger(JiraBuilder.name)
    private BlockingQueue<QueueTaskFuture<? extends AbstractBuild>> lastScheduledBuild = new ArrayBlockingQueue<>(1)

    @Inject
    Jenkins jenkins

    @Override
    void commentCreated(Map issue, Map comment) {
        def jobs = jenkins.getAllItems(AbstractProject).findAll { it.getTrigger(JiraBuilderTrigger) }
        if (jobs) {
            LOGGER.info("Found jobs: ${jobs.collect { it.name }}")
            for (job in jobs) {
                JiraBuilderTrigger trigger = job.getTrigger(JiraBuilderTrigger)
                if (trigger.commentPattern) {
                    if (!(comment.body ==~ trigger.commentPattern)) {
                        break
                    }
                }
                lastScheduledBuild.put(job.scheduleBuild2(0, new JiraBuilderTrigger.JiraBuilderTriggerCause(), new ParametersAction(
                        new StringParameterValue("description", issue.fields.description))))

            }
        } else {
            LOGGER.fine("Couldn't find any jobs that have JiraBuildTrigger configured")
        }
    }

    public AbstractBuild getLastScheduledBuild(long timeout, TimeUnit timeUnit) {
        def build = lastScheduledBuild.poll(timeout, timeUnit)
        if (build) {
            return build.get()
        } else {
            return null
        }
    }
}
