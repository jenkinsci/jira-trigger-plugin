package com.ceilfors.jenkins.plugins.jirabuilder
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import com.ceilfors.jenkins.plugins.jirabuilder.parameter.IssueAttributePathParameterMapping
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhookContext
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhookListener
import com.google.inject.Singleton
import groovy.util.logging.Log
import hudson.model.*
import hudson.model.queue.QueueTaskFuture
import jenkins.model.Jenkins

import javax.inject.Inject
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
/**
 * @author ceilfors
 */
@Singleton
@Log
class JiraBuilder implements JiraWebhookListener {

    private BlockingQueue<QueueTaskFuture<? extends AbstractBuild>> lastScheduledBuild = new ArrayBlockingQueue<>(1)

    private Jenkins jenkins
    private JiraClient jira

    @Inject
    public JiraBuilder(Jenkins jenkins, JiraClient jira) {
        this.jenkins = jenkins
        this.jira = jira
    }

    @Override
    void commentCreated(JiraWebhookContext jiraWebhookContext) {
        def jobs = jenkins.getAllItems(AbstractProject).findAll { it.getTrigger(JiraBuilderTrigger) }
        if (jobs) {
            log.fine("Found jobs: ${jobs.collect { it.name }}")
            for (job in jobs) {
                JiraBuilderTrigger trigger = job.getTrigger(JiraBuilderTrigger)
                if (trigger.commentPattern) {
                    if (!(jiraWebhookContext.eventBody.body ==~ trigger.commentPattern)) {
                        log.fine("[${job.fullName}] commentPattern doesn't match with the comment body, not scheduling build")
                        break
                    }
                }
                if (trigger.jqlFilter) {
                    if (!jira.validateIssueKey(jiraWebhookContext.issueKey, trigger.jqlFilter)) {
                        log.fine("[${job.fullName}] jqlFilter doesn't match with the JQL filter, not scheduling build")
                        break
                    }
                }
                if (trigger.parameterMappings) {
                    def issue = jira.getIssueMap(jiraWebhookContext.issueKey)
                    lastScheduledBuild.put(job.scheduleBuild2(0, new JiraBuilderTrigger.JiraBuilderTriggerCause(),
                            new ParametersAction(collectParameterValues(trigger, issue))))
                } else {
                    lastScheduledBuild.put(job.scheduleBuild2(0, new JiraBuilderTrigger.JiraBuilderTriggerCause()))
                }
            }
        } else {
            log.fine("Couldn't find any jobs that have JiraBuildTrigger configured")
        }
    }

    private List<ParameterValue> collectParameterValues(JiraBuilderTrigger jiraBuilderTrigger, Map issue) {
        jiraBuilderTrigger.parameterMappings.collect {
            if (it instanceof IssueAttributePathParameterMapping) {
                IssueAttributePathParameterMapping parameterMapping = it

                def attributeValue = GroovyUtils.resolveProperty(issue, parameterMapping.issueAttributePath)
                if (attributeValue) {
                    return new StringParameterValue(parameterMapping.jenkinsParameter, attributeValue as String)
                } else {
                    LOGGER.warning("Can't resolve attribute ${parameterMapping.issueAttributePath} from JIRA issue. Example: fields.description, key, fields.project.key")
                    return null
                }
            } else {
                throw new UnsupportedOperationException("Unsupported parameter mapping ${it.class}")
            }
        } - null
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
