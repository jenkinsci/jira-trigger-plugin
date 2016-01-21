package com.ceilfors.jenkins.plugins.jiratrigger

import hudson.Extension
import hudson.model.BuildableItem
import hudson.model.Cause
import hudson.model.Item
import hudson.triggers.TriggerDescriptor
import org.kohsuke.stapler.DataBoundConstructor
/**
 * @author ceilfors
 */
class JiraChangelogTrigger {

    @DataBoundConstructor
    JiraChangelogTrigger() {
    }

    @Extension
    static class DescriptorImpl extends TriggerDescriptor {

        public boolean isApplicable(Item item) {
            return item instanceof BuildableItem
        }

        public String getDisplayName() {
            return "Build when an issue is updated in JIRA"
        }
    }

    static class JiraChangelogTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            return "JIRA issue is updated"
        }
    }
}
