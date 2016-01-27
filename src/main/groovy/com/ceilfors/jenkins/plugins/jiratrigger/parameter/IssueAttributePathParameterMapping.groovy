package com.ceilfors.jenkins.plugins.jiratrigger.parameter

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
        this.issueAttributePath = issueAttributePath.trim()
    }

    String getIssueAttributePath() {
        return issueAttributePath
    }

    @SuppressWarnings("UnnecessaryQualifiedReference") // Can't remove qualifier, IntelliJ bug?
    @Extension
    static class IssueAttributePathParameterMappingDescriptor extends ParameterMapping.ParameterMappingDescriptor {

        public static final String DISPLAY_NAME = "Issue Attribute Path"

        @Override
        String getDisplayName() {
            DISPLAY_NAME
        }
    }
}
