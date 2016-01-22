package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Comment
import hudson.model.AbstractProject
/**
 * @author ceilfors
 */
interface JiraTriggerListener {
    void buildScheduled(Comment comment, Collection<? extends AbstractProject> projects)
    void buildScheduled(ChangelogGroup changelog, Collection<? extends AbstractProject> projects)
    void buildNotScheduled(Comment comment)
    void buildNotScheduled(ChangelogGroup changelogGroup)
}
