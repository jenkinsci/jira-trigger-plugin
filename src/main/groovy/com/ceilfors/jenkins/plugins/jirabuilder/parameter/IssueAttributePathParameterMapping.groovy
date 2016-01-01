package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import hudson.Extension
import org.kohsuke.stapler.DataBoundConstructor
/**
 * @author ceilfors
 */
class IssueAttributePathParameterMapping extends ParameterMapping {

    private final String issueAttributePath

    @DataBoundConstructor
    IssueAttributePathParameterMapping(String jenkinsParameter, String issueAttributePath) {
        super(jenkinsParameter)
        this.issueAttributePath = issueAttributePath
    }

    String getIssueAttributePath() {
        return issueAttributePath
    }

    @Extension
    static class IssueAttributePathParameterMappingDescriptor extends ParameterMapping.ParameterMappingDescriptor {

        @Override
        String getDisplayName() {
            "Issue Attribute Path"
        }
    }
}
