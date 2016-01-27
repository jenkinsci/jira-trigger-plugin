package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.ChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.BuildableItem
import hudson.model.Cause
import hudson.model.Item
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import jenkins.model.Jenkins
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import javax.inject.Inject
/**
 * @author ceilfors
 */
@Log
class JiraChangelogTrigger extends Trigger<BuildableItem> {

    private int quietPeriod
    private String jqlFilter = ""
    private List<ChangelogMatcher> changelogMatchers = []

    @DataBoundConstructor
    JiraChangelogTrigger() {
    }

    void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod
    }

    List<ChangelogMatcher> getChangelogMatchers() {
        return changelogMatchers
    }

    @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins DataBoundSetter
    @DataBoundSetter
    void setChangelogMatchers(List<ChangelogMatcher> changelogMatchers) {
        this.changelogMatchers = changelogMatchers
    }

    String getJqlFilter() {
        return jqlFilter
    }

    @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins DataBoundSetter
    @DataBoundSetter
    void setJqlFilter(String jqlFilter) {
        this.jqlFilter = jqlFilter
    }

    @Override
    DescriptorImpl getDescriptor() {
        return super.getDescriptor() as DescriptorImpl
    }

    boolean run(Issue issue, ChangelogGroup changelogGroup) {
        for (changelogMatcher in changelogMatchers) {
            if (!changelogMatcher.matches(changelogGroup)) {
                log.fine("[${job.fullName}] - Not scheduling build: The issue ${issue.key} changelog doesn't match with the changelog matcher ${changelogMatcher}")
                return false
            }
        }
        if (jqlFilter) {
            if (!descriptor.jiraClient.validateIssueKey(issue.key, jqlFilter)) {
                log.fine("[${job.fullName}] - Not scheduling build: The issue ${issue.key} doesn't match with the jqlFilter [$jqlFilter]")
                return false
            }
        }
        return job.scheduleBuild(quietPeriod, new JiraChangelogTriggerCause())
    }

    @Extension
    static class DescriptorImpl extends TriggerDescriptor {

        @Inject
        private JiraClient jiraClient

        @Inject
        private Jenkins jenkins

        public boolean isApplicable(Item item) {
            return item instanceof BuildableItem
        }

        public String getDisplayName() {
            return "Build when an issue is updated in JIRA"
        }

        @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins jelly
        public List<ChangelogMatcher.ChangelogMatcherDescriptor> getChangelogMatcherDescriptors() {
            return jenkins.getDescriptorList(ChangelogMatcher)
        }
    }

    static class JiraChangelogTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            return "JIRA issue is updated"
        }
    }
}
