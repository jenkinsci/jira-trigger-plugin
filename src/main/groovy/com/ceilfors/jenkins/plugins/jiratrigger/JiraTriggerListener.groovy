package com.ceilfors.jenkins.plugins.jiratrigger
import com.atlassian.jira.rest.client.api.domain.Comment
import hudson.model.AbstractProject
/**
 * @author ceilfors
 */
interface JiraTriggerListener {
    void buildScheduled(Comment comment, Collection<? extends AbstractProject> projects)
    void buildNotScheduled(Comment comment)
}
