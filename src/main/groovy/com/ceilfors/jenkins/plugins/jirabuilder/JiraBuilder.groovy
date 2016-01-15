package com.ceilfors.jenkins.plugins.jirabuilder
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhookListener
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.WebhookCommentEvent
import com.google.inject.Singleton
import groovy.util.logging.Log
import hudson.model.AbstractProject
import jenkins.model.Jenkins

import javax.inject.Inject
/**
 * @author ceilfors
 */
@Singleton
@Log
class JiraBuilder implements JiraWebhookListener {

    private Jenkins jenkins
    private JiraClient jira
    private Set<JiraBuilderListener> jiraBuilderListeners = []
    private int quietPeriod = 0

    @Inject
    public JiraBuilder(Jenkins jenkins, JiraClient jira) {
        this.jenkins = jenkins
        this.jira = jira
    }

    @Inject
    private void setJiraBuilderListeners(Set<JiraBuilderListener> jiraBuilderListeners) {
        this.jiraBuilderListeners.addAll(jiraBuilderListeners)
    }

    void addJiraBuilderListener(JiraBuilderListener jiraBuilderListener) {
        jiraBuilderListeners << jiraBuilderListener
    }

    void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod
    }

    @Override
    void commentCreated(WebhookCommentEvent commentEvent) {
        def jobs = jenkins.getAllItems(AbstractProject).findAll { it.getTrigger(JiraCommentBuilderTrigger) }
        if (jobs) {
            log.finest("Found jobs with JiraCommentBuilderTrigger configuration: ${jobs.collect { it.name }}")
            List<AbstractProject> scheduledProjects = []
            for (job in jobs) {
                JiraCommentBuilderTrigger trigger = job.getTrigger(JiraCommentBuilderTrigger)
                trigger.setQuietPeriod(quietPeriod)
                boolean scheduled = trigger.run(commentEvent.comment)
                if (scheduled) {
                    scheduledProjects << job
                }
            }
            if (scheduledProjects) {
                jiraBuilderListeners*.buildScheduled(commentEvent.comment, scheduledProjects)
            } else {
                jiraBuilderListeners*.buildNotScheduled(commentEvent.comment)
            }
        } else {
            log.fine("Couldn't find any jobs that have JiraCommentBuilderTrigger configured")
        }
    }
}
