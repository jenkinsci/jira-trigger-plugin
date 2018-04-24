package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import hudson.model.AbstractProject

import javax.inject.Inject
/**
 * @author ceilfors
 */
class JiraCommentReplier implements JiraTriggerListener {

    @Inject
    JiraClient jiraClient

    @Inject
    JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration

    @Override
    void buildScheduled(Issue issue, Collection<? extends AbstractProject> projects) {
        if (jiraTriggerGlobalConfiguration.jiraCommentReply) {
            jiraClient.addComment(issue.key, 'Build is scheduled for: ' + projects*.absoluteUrl.join(', '))
        }
    }

    @Override
    void buildNotScheduled(Issue issue) {
    }
}
