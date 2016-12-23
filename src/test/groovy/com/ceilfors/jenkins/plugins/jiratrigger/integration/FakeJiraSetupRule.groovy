package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentReplier
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import org.junit.rules.ExternalResource

import static com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger.JiraChangelogTriggerDescriptor
import static com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger.JiraCommentTriggerDescriptor

/**
 * @author ceilfors
 */
class FakeJiraSetupRule extends ExternalResource {

    JenkinsRunner jenkinsRunner

    FakeJiraSetupRule(JenkinsRunner jenkinsRunner) {
        this.jenkinsRunner = jenkinsRunner
    }

    protected void before() throws Throwable {
        def jiraClient = new FakeJiraClient()

        // KLUDGE: Could not find a better way to override Guice injection
        jenkinsRunner.jenkins.getDescriptorByType(JiraChangelogTriggerDescriptor).jiraClient = jiraClient
        jenkinsRunner.jenkins.getDescriptorByType(JiraCommentTriggerDescriptor).jiraClient = jiraClient
    }

    static class FakeJiraClient implements JiraClient {

        @Override
        void addComment(String issueKey, String comment) {
        }

        @Override
        boolean validateIssueKey(String issueKey, String jqlFilter) {
            return true
        }
    }
}
