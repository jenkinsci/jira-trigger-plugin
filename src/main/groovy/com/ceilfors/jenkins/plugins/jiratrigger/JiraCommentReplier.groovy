package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Comment
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraUtils
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
    void buildScheduled(Comment comment, Collection<? extends AbstractProject> projects) {
        if (jiraTriggerGlobalConfiguration.jiraCommentReply) {
            def issueId = JiraUtils.getIssueIdFromComment(comment)
            jiraClient.addComment(issueId, "Build is scheduled for: " + projects.collect { it.absoluteUrl })
        }
    }

    @Override
    void buildScheduled(ChangelogGroup changelog, Collection<? extends AbstractProject> projects) {
    }

    @Override
    void buildNotScheduled(Comment comment) {
    }

    @Override
    void buildNotScheduled(ChangelogGroup changelogGroup) {

    }
}
