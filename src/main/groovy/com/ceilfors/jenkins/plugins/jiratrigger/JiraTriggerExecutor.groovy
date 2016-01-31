package com.ceilfors.jenkins.plugins.jiratrigger
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhookListener
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookChangelogEvent
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookCommentEvent
import com.google.inject.Singleton
import groovy.util.logging.Log
import hudson.model.AbstractProject
import jenkins.model.Jenkins

import javax.inject.Inject
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author ceilfors
 */
@Singleton
@Log
class JiraTriggerExecutor implements JiraWebhookListener {

    private Jenkins jenkins
    private JiraClient jira
    private List<JiraTriggerListener> jiraTriggerListeners = new CopyOnWriteArrayList<>()
    private int quietPeriod = 0

    @Inject
    public JiraTriggerExecutor(Jenkins jenkins, JiraClient jira) {
        this.jenkins = jenkins
        this.jira = jira
    }

    @Inject
    private void setJiraTriggerListeners(Set<JiraTriggerListener> jiraTriggerListeners) {
        this.jiraTriggerListeners.addAll(jiraTriggerListeners)
    }

    void addJiraTriggerListener(JiraTriggerListener jiraTriggerListener) {
        jiraTriggerListeners << jiraTriggerListener
    }

    void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod
    }

    @Override
    void commentCreated(WebhookCommentEvent commentEvent) {
        def jobs = jenkins.getAllItems(AbstractProject).findAll { it.getTrigger(JiraCommentTrigger) }
        if (jobs) {
            log.finest("Found jobs with JiraCommentTrigger configuration: ${jobs.collect { it.name }}")
            List<AbstractProject> scheduledProjects = []
            for (job in jobs) {
                JiraCommentTrigger trigger = job.getTrigger(JiraCommentTrigger)
                trigger.setQuietPeriod(quietPeriod)
                boolean scheduled = trigger.run(commentEvent.issue, commentEvent.comment)
                if (scheduled) {
                    scheduledProjects << job
                }
            }
            if (scheduledProjects) {
                jiraTriggerListeners*.buildScheduled(commentEvent.issue, scheduledProjects)
            } else {
                jiraTriggerListeners*.buildNotScheduled(commentEvent.issue)
            }
        } else {
            log.fine("Couldn't find any jobs that have JiraCommentTrigger configured")
        }
    }

    @Override
    void changelogCreated(WebhookChangelogEvent changelogEvent) {
        def jobs = jenkins.getAllItems(AbstractProject).findAll { it.getTrigger(JiraChangelogTrigger) }
        if (jobs) {
            log.finest("Found jobs with JiraChangelogTrigger configuration: ${jobs.collect { it.name }}")
            List<AbstractProject> scheduledProjects = []
            for (job in jobs) {
                JiraChangelogTrigger trigger = job.getTrigger(JiraChangelogTrigger)
                trigger.setQuietPeriod(quietPeriod)
                boolean scheduled = trigger.run(changelogEvent.issue, changelogEvent.changelog)
                if (scheduled) {
                    scheduledProjects << job
                }
            }
            if (scheduledProjects) {
                jiraTriggerListeners*.buildScheduled(changelogEvent.issue, scheduledProjects)
            } else {
                jiraTriggerListeners*.buildNotScheduled(changelogEvent.issue)
            }
        } else {
            log.fine("Couldn't find any jobs that have JiraChangelogTrigger configured")
        }
    }
}
