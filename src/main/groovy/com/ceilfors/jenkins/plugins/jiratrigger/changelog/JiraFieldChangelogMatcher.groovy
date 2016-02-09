package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.FieldType
import com.atlassian.jira.rest.client.api.domain.IssueFieldId
import groovy.transform.ToString
import hudson.Extension
import hudson.Util
import hudson.util.ComboBoxModel
import org.kohsuke.stapler.DataBoundConstructor

/**
 * @author ceilfors
 */
@ToString(includeFields = true)
class JiraFieldChangelogMatcher extends ChangelogMatcher {

    private final String field
    private final String newValue
    private final String oldValue

    @DataBoundConstructor
    JiraFieldChangelogMatcher(String field, String newValue, String oldValue) {
        this.field = field.trim()
        this.newValue = newValue.trim()
        this.oldValue = oldValue.trim()
    }

    String getField() {
        return field
    }

    String getNewValue() {
        return newValue
    }

    String getOldValue() {
        return oldValue
    }

    @Override
    boolean matches(ChangelogGroup changelogGroup) {
        changelogGroup.items.find {
            it.fieldType == FieldType.JIRA &&
                    it.field == field &&
                    Util.fixNull(it.toString).equalsIgnoreCase(newValue) &&
                    (oldValue ? Util.fixNull(it.fromString).equalsIgnoreCase(oldValue) : true)
        }
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
