package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
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
    private int quietPeriod = 3

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
        return scheduleBuildsInternal(JiraCommentTrigger, issue, comment)
    }

    List<AbstractProject> scheduleBuilds(Issue issue, ChangelogGroup changelogGroup) {
        return scheduleBuildsInternal(JiraChangelogTrigger, issue, changelogGroup)
    }

    /**
     * @return the scheduled projects
     */
    private List<AbstractProject> scheduleBuildsInternal(
            Class<? extends JiraTrigger> triggerClass, Issue issue, Object jiraObject) {
        def projects = getProjectsWithTrigger(triggerClass)
        List<AbstractProject> scheduledProjects = []
        for (project in projects) {
            JiraTrigger trigger = project.getTrigger(triggerClass)
            trigger.setQuietPeriod(quietPeriod)
            boolean scheduled = trigger.run(issue, jiraObject)
            if (scheduled) {
                scheduledProjects << project
            }
        }
        return scheduledProjects
    }

    private List<AbstractProject> getProjectsWithTrigger(Class<? extends JiraTrigger> triggerClass) {
        def projects = jenkins.getAllItems(AbstractProject).findAll { it.getTrigger(triggerClass) }
        if (projects) {
            log.finest("Found projects with ${triggerClass.simpleName} configuration: ${projects*.name}")
            return projects
        } else {
            log.fine("Couldn't find any projects that have ${triggerClass.simpleName} configured")
            return []
        }
    }
}
