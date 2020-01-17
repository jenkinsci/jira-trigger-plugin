package com.ceilfors.jenkins.plugins.jiratrigger.jira


import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import hudson.Extension
import org.kohsuke.stapler.DataBoundConstructor
/**
 * @author ceilfors
 */
@ToString(includeSuper = true)
@EqualsAndHashCode(callSuper = true)
class FieldsIssueMatcher extends IssueMatcher {

    @DataBoundConstructor
    FieldsIssueMatcher(String field, String value) {
        super(field.trim(), value.trim())
    }

    @SuppressWarnings('UnnecessaryQualifiedReference') // Can't remove qualifier, IntelliJ bug?
    @Extension
    static class FieldsIssueMatcherDescriptor extends IssueMatcher.IssueMatcherDescriptor {

        public static final String DISPLAY_NAME = 'Fields Issue Matcher'

        @Override
        String getDisplayName() {
            DISPLAY_NAME
        }
    }
}
