package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.FieldType
import com.atlassian.jira.rest.client.api.domain.IssueFieldId
import groovy.transform.ToString
import hudson.Extension
import hudson.util.ComboBoxModel
import org.kohsuke.stapler.DataBoundConstructor
/**
 * @author ceilfors
 */
@ToString(includeFields = true)
class JiraFieldChangelogMatcher extends ChangelogMatcher {

    @DataBoundConstructor
    JiraFieldChangelogMatcher(String field, String newValue, String oldValue, boolean comparingNewValue, boolean comparingOldValue) {
        super(FieldType.JIRA, field.trim(), newValue.trim(), oldValue.trim(), comparingNewValue, comparingOldValue)
    }

    @SuppressWarnings("UnnecessaryQualifiedReference") // Can't remove qualifier, IntelliJ bug?
    @Extension
    static class JiraFieldChangelogMatcherDescriptor extends ChangelogMatcher.ChangelogMatcherDescriptor {

        public static final String DISPLAY_NAME = "JIRA Field Matcher"

        @Override
        String getDisplayName() {
            DISPLAY_NAME
        }

        @SuppressWarnings("GroovyUnusedDeclaration") // jelly
        public ComboBoxModel doFillFieldItems() {
            return new ComboBoxModel(IssueFieldId.ids().toList())
        }
    }
}
