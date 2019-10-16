package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.Cause
import org.kohsuke.stapler.DataBoundConstructor

/**
 * Responsible for processing <tt>String</tt> and determine if a job should be scheduled.
 *
 * @author ceilfors
 */
@Log
class JiraIssueCreatedTrigger extends JiraTrigger<String> {

    @SuppressWarnings('UnnecessaryConstructor')
    @DataBoundConstructor
    JiraIssueCreatedTrigger() {
    }

    @Override
    boolean filter(Issue issue, String issueKey) {
        true
    }

    @Override
    Cause getCause(Issue issue, String issueKey) {
        new JiraIssueCreatedTriggerCause()
    }

    @SuppressWarnings('UnnecessaryQualifiedReference')
    @Extension
    static class JiraIssueCreatedTriggerDescriptor extends JiraTrigger.JiraTriggerDescriptor {
        @Override
        String getDisplayName() {
            'Build when a issue is created in JIRA'
        }
    }

    static class JiraIssueCreatedTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            'JIRA issue is created'
        }
    }
}
