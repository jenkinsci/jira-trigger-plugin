package com.ceilfors.jenkins.plugins.jiratrigger
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.BuildableItem
import hudson.model.Cause
import hudson.model.Item
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import org.kohsuke.stapler.DataBoundConstructor
/**
 * @author ceilfors
 */
@Log
class JiraChangelogTrigger extends Trigger<BuildableItem> {

    private int quietPeriod

    @DataBoundConstructor
    JiraChangelogTrigger() {
    }

    void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod
    }

    boolean run(ChangelogGroup changelogGroup) {
        return job.scheduleBuild(quietPeriod, new JiraChangelogTriggerCause())
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
