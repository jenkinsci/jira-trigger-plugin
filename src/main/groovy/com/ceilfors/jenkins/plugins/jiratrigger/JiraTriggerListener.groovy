package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import hudson.model.AbstractProject
/**
 * @author ceilfors
 */
interface JiraTriggerListener {
    void buildScheduled(Issue issue, Collection<? extends AbstractProject> projects)
    void buildNotScheduled(Issue issue)
}
