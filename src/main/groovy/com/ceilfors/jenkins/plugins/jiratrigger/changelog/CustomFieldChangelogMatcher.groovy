package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.FieldType
import groovy.transform.ToString
import hudson.Extension
import org.kohsuke.stapler.DataBoundConstructor
/**
 * @author ceilfors
 */
@ToString(includeFields = true)
class CustomFieldChangelogMatcher extends ChangelogMatcher {

    @DataBoundConstructor
    CustomFieldChangelogMatcher(String field, String newValue, String oldValue, boolean comparingNewValue, boolean comparingOldValue) {
        super(FieldType.CUSTOM, field.trim(), newValue.trim(), oldValue.trim(), comparingNewValue, comparingOldValue)
    }

    @SuppressWarnings("UnnecessaryQualifiedReference") // Can't remove qualifier, IntelliJ bug?
    @Extension
    static class CustomFieldChangelogMatcherDescriptor extends ChangelogMatcher.ChangelogMatcherDescriptor {

        public static final String DISPLAY_NAME = "Custom Field Matcher"

        @Override
        String getDisplayName() {
            DISPLAY_NAME
        }
    }
}
