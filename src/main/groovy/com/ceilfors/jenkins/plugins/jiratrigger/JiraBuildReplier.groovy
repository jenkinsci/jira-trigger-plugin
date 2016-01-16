package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Comment
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraUtils
import hudson.model.AbstractProject

import javax.inject.Inject

/**
 * @author ceilfors
 */
class JiraBuildReplier implements JiraTriggerListener {

    @Inject
    JiraClient jiraClient

    @Override
    void buildScheduled(Comment comment, Collection<? extends AbstractProject> projects) {
        def issueId = JiraUtils.getIssueIdFromComment(comment)
        jiraClient.addComment(issueId, "Build is scheduled for: " + projects.collect { it.absoluteUrl} )
    }

    @Override
    void buildNotScheduled(Comment comment) {
    }
}
