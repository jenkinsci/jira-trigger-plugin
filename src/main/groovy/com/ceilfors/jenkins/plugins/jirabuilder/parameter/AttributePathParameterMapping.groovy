package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import hudson.Extension
import org.kohsuke.stapler.DataBoundConstructor
/**
 * @author ceilfors
 */
class AttributePathParameterMapping extends ParameterMapping {

    private String jiraAttributePath

    @DataBoundConstructor
    AttributePathParameterMapping(String jenkinsParameter, String jiraAttributePath) {
        super(jenkinsParameter)
        this.jiraAttributePath = jiraAttributePath
    }

    String getJiraAttributePath() {
        return jiraAttributePath
    }

    @Extension
    static class AttributePathParameterMappingDescriptor extends ParameterMapping.ParameterMappingDescriptor {

        @Override
        String getDisplayName() {
            "JIRA Attribute Path"
        }
    }
}
