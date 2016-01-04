package com.ceilfors.jenkins.plugins.jirabuilder
import com.atlassian.jira.rest.client.api.domain.Comment
import hudson.model.AbstractProject
/**
 * @author ceilfors
 */
interface JiraBuilderListener {
    def buildScheduled(Comment comment, Collection<? extends AbstractProject> projects)
    def buildNotScheduled(Comment comment)
}
