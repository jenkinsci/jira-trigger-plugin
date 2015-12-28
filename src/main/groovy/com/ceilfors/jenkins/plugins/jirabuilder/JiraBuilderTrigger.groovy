package com.ceilfors.jenkins.plugins.jirabuilder
import com.ceilfors.jenkins.plugins.jirabuilder.parameter.ParameterMapping
import hudson.Extension
import hudson.model.AbstractProject
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
class JiraBuilderTrigger extends Trigger<AbstractProject> {

    private String commentPattern = ""
    private String jqlFilter = ""
    private List<ParameterMapping> parameterMappings = []

    @DataBoundConstructor
    JiraBuilderTrigger() {
    }

    String getCommentPattern() {
        return commentPattern
    }

    @DataBoundSetter
    void setCommentPattern(String commentPattern) {
        this.commentPattern = commentPattern
    }

    List<ParameterMapping> getParameterMappings() {
        return Collections.unmodifiableList(parameterMappings)
    }

    @DataBoundSetter
    void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings
    }

    String getJqlFilter() {
        return jqlFilter
    }

    @DataBoundSetter
    void setJqlFilter(String jqlFilter) {
        this.jqlFilter = jqlFilter
    }

    @Override
    DescriptorImpl getDescriptor() {
        return super.getDescriptor() as DescriptorImpl
    }

    @Extension
    static class DescriptorImpl extends TriggerDescriptor {

        @Inject
        private Jenkins jenkins

        public boolean isApplicable(Item item) {
            return item instanceof BuildableItem
        }

        public String getDisplayName() {
            return "JIRA Builder"
        }

        public List<ParameterMapping.ParameterMappingDescriptor> getParameterMappingDescriptors() {
            return jenkins.getDescriptorList(ParameterMapping)
        }
    }

    static class JiraBuilderTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            return "JIRA Builder"
        }
    }
}
