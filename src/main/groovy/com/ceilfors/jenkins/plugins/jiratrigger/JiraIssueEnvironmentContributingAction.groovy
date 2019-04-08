package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.EnvironmentContributingAction
import hudson.model.Run

import javax.annotation.Nonnull

/**
 * @author ceilfors
 */
class JiraIssueEnvironmentContributingAction implements EnvironmentContributingAction {

    String issueKey

    JiraIssueEnvironmentContributingAction(Issue issue) {
        this.issueKey = issue.key
    }

    @Override
    @Deprecated
    void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        env.put('JIRA_ISSUE_KEY', issueKey)
    }

    @Override
    void buildEnvironment(@Nonnull Run<?, ?> run, @Nonnull EnvVars env) {
        env.put('JIRA_ISSUE_KEY', issueKey)
    }

    @Override
    String getIconFileName() {
        null
    }

    @Override
    String getDisplayName() {
        null
    }

    @Override
    String getUrlName() {
        null
    }
}
