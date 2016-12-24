package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import org.junit.rules.ExternalResource

import static com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger.JiraChangelogTriggerDescriptor
import static com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger.JiraCommentTriggerDescriptor

/**
 * @author ceilfors
 */
class FakeJiraSetupRule extends ExternalResource {

    JenkinsRunner jenkinsRunner
    JiraClient jiraClient

    FakeJiraSetupRule(JenkinsRunner jenkinsRunner, JiraClient jiraClient) {
        this.jenkinsRunner = jenkinsRunner
        this.jiraClient = jiraClient
    }

    protected void before() throws Throwable {
        // KLUDGE: Could not find a better way to override Guice injection
        jenkinsRunner.jenkins.getDescriptorByType(JiraChangelogTriggerDescriptor).jiraClient = jiraClient
        jenkinsRunner.jenkins.getDescriptorByType(JiraCommentTriggerDescriptor).jiraClient = jiraClient
    }
}
