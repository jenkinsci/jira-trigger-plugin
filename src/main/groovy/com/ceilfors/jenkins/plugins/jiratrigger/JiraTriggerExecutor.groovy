package com.ceilfors.jenkins.plugins.jiratrigger

import org.codehaus.jettison.json.JSONObject
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhookListener
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookChangelogEvent
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookIssueCreatedEvent
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookCommentEvent
import com.google.inject.Singleton
import groovy.util.logging.Log
import hudson.model.AbstractProject
import jenkins.model.Jenkins

import javax.inject.Inject
import java.util.concurrent.CopyOnWriteArrayList

import static com.ceilfors.jenkins.plugins.jiratrigger.JiraTrigger.JiraTriggerDescriptor

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
    void commentCreated(WebhookCommentEvent commentEvent, JSONObject issueJsonObject) {
        List<AbstractProject> scheduledProjects = scheduleBuilds(commentEvent.issue, issueJsonObject, commentEvent.comment)
        fireListeners(scheduledProjects, commentEvent.issue)
    }

    @Override
    void changelogCreated(WebhookChangelogEvent changelogEvent, JSONObject issueJsonObject) {
        List<AbstractProject> scheduledProjects = scheduleBuilds(changelogEvent.issue, issueJsonObject, changelogEvent.changelog)
        fireListeners(scheduledProjects, changelogEvent.issue)
    }

    @Override
    void issueCreated(WebhookIssueCreatedEvent issueCreatedEvent, JSONObject issueJsonObject) {
        List<AbstractProject> scheduledProjects = scheduleBuilds(issueCreatedEvent.issue, issueJsonObject)
        fireListeners(scheduledProjects, issueCreatedEvent.issue)
    }

    private void fireListeners(List<AbstractProject> scheduledProjects, Issue issue) {
        if (scheduledProjects) {
            jiraTriggerListeners*.buildScheduled(issue, scheduledProjects)
        } else {
            jiraTriggerListeners*.buildNotScheduled(issue)
        }
    }

    List<AbstractProject> scheduleBuilds(Issue issue, JSONObject issueJsonObject, Comment comment) {
        scheduleBuildsInternal(JiraCommentTrigger, issue, issueJsonObject, comment)
    }

    List<AbstractProject> scheduleBuilds(Issue issue, JSONObject issueJsonObject) {
        scheduleBuildsInternal(JiraIssueCreatedTrigger, issue, issueJsonObject, issue.key)
    }

    List<AbstractProject> scheduleBuilds(Issue issue, JSONObject issueJsonObject, ChangelogGroup changelogGroup) {
        scheduleBuildsInternal(JiraChangelogTrigger, issue, issueJsonObject, changelogGroup)
    }

    /**
     * @return the scheduled projects
     */
    private List<AbstractProject> scheduleBuildsInternal(
        Class<? extends JiraTrigger> triggerClass, Issue issue, JSONObject issueJsonObject, Object jiraObject) {
        List<AbstractProject> scheduledProjects = []
        List<? extends JiraTrigger> triggers = getTriggers(triggerClass)
        for (trigger in triggers) {
            boolean scheduled = trigger.run(issue, issueJsonObject, jiraObject)
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
