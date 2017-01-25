package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhookListener
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookChangelogEvent
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookCommentEvent
import com.google.inject.Singleton
import groovy.util.logging.Log
import hudson.model.AbstractProject
import jenkins.model.Jenkins

import javax.inject.Inject
import java.util.concurrent.CopyOnWriteArrayList

import static com.ceilfors.jenkins.plugins.jiratrigger.JiraTrigger.*

/**
 * @author ceilfors
 */
@Singleton
@Log
class JiraTriggerExecutor implements JiraWebhookListener {

    private final Jenkins jenkins
    private final List<JiraTriggerListener> jiraTriggerListeners = new CopyOnWriteArrayList<>()

    @Inject
    JiraTriggerExecutor(Jenkins jenkins) {
        this.jenkins = jenkins
    }

    @Inject
    private void setJiraTriggerListeners(Set<JiraTriggerListener> jiraTriggerListeners) {
        this.jiraTriggerListeners.addAll(jiraTriggerListeners)
    }

    void addJiraTriggerListener(JiraTriggerListener jiraTriggerListener) {
        jiraTriggerListeners << jiraTriggerListener
    }

    @Override
    void commentCreated(WebhookCommentEvent commentEvent) {
        List<AbstractProject> scheduledProjects = scheduleBuilds(commentEvent.issue, commentEvent.comment)
        fireListeners(scheduledProjects, commentEvent.issue)
    }

    @Override
    void changelogCreated(WebhookChangelogEvent changelogEvent) {
        List<AbstractProject> scheduledProjects = scheduleBuilds(changelogEvent.issue, changelogEvent.changelog)
        fireListeners(scheduledProjects, changelogEvent.issue)
    }

    private void fireListeners(List<AbstractProject> scheduledProjects, Issue issue) {
        if (scheduledProjects) {
            jiraTriggerListeners*.buildScheduled(issue, scheduledProjects)
        } else {
            jiraTriggerListeners*.buildNotScheduled(issue)
        }
    }

    List<AbstractProject> scheduleBuilds(Issue issue, Comment comment) {
        scheduleBuildsInternal(JiraCommentTrigger, issue, comment)
    }

    List<AbstractProject> scheduleBuilds(Issue issue, ChangelogGroup changelogGroup) {
        scheduleBuildsInternal(JiraChangelogTrigger, issue, changelogGroup)
    }

    /**
     * @return the scheduled projects
     */
    private List<AbstractProject> scheduleBuildsInternal(
            Class<? extends JiraTrigger> triggerClass, Issue issue, Object jiraObject) {
        List<AbstractProject> scheduledProjects = []
        List<? extends JiraTrigger> triggers = getTriggers(triggerClass)
        for (trigger in triggers) {
            boolean scheduled = trigger.run(issue, jiraObject)
            if (scheduled) {
                scheduledProjects << trigger.job
            }
        }
        scheduledProjects
    }

    private List<? extends JiraTrigger> getTriggers(Class<? extends JiraTrigger> triggerClass) {
        JiraTriggerDescriptor descriptor = jenkins.getDescriptor(triggerClass) as JiraTriggerDescriptor
        List<? extends JiraTrigger> triggers = descriptor.allTriggers()
        if (!triggers) {
            log.fine("Couldn't find any projects that have ${triggerClass.simpleName} configured")
        }
        triggers
    }
}
