package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.ChangelogMatcher
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.Cause
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

/**
 * Responsible for processing <tt>ChangelogGroup</tt> and determine if a build should be scheduled.
 *
 * @author ceilfors
 */
@Log
class JiraChangelogTrigger extends JiraTrigger<ChangelogGroup> {

    @DataBoundSetter
    List<ChangelogMatcher> changelogMatchers = []

    @SuppressWarnings('UnnecessaryConstructor')
    @DataBoundConstructor
    JiraChangelogTrigger() {
    }

    @Override
    boolean filter(Issue issue, ChangelogGroup changelogGroup) {
        for (changelogMatcher in changelogMatchers) {
            if (!changelogMatcher.matches(changelogGroup)) {
                log.fine("[${job.fullName}] - Not scheduling build: The changelog [${changelogGroup}] doesn't " +
                        "match with the changelog matcher [${changelogMatcher}]")
                return false
            }
        }
        true
    }

    @Override
    Cause getCause(Issue issue, ChangelogGroup changelogGroup) {
        new JiraChangelogTriggerCause()
    }

    @SuppressWarnings('UnnecessaryQualifiedReference')
    @Extension
    static class JiraChangelogTriggerDescriptor extends JiraTrigger.JiraTriggerDescriptor {

        @Override
        String getDisplayName() {
            'Build when an issue is updated in JIRA'
        }

        @SuppressWarnings('GroovyUnusedDeclaration') // Jenkins jelly
        List<ChangelogMatcher.ChangelogMatcherDescriptor> getChangelogMatcherDescriptors() {
            jenkins.getDescriptorList(ChangelogMatcher)
        }
    }

    static class JiraChangelogTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            'JIRA issue is updated'
        }
    }
}
