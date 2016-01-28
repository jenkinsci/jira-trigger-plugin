package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
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
class BuiltInFieldChangelogMatcher extends ChangelogMatcher {

    private final String field
    private final String newValue

    @DataBoundConstructor
    BuiltInFieldChangelogMatcher(String field, String newValue) {
        this.field = field.trim()
        this.newValue = newValue.trim()
    }

    String getField() {
        return field
    }

    String getNewValue() {
        return newValue
    }

    @Override
    boolean matches(ChangelogGroup changelogGroup) {
        changelogGroup.items.find {
            it.fieldType == FieldType.JIRA &&
                    it.field == field &&
                    it.toString.equalsIgnoreCase(newValue)
        }
    }

    @SuppressWarnings("UnnecessaryQualifiedReference") // Can't remove qualifier, IntelliJ bug?
    @Extension
    static class BuiltInFieldChangelogMatcherDescriptor extends ChangelogMatcher.ChangelogMatcherDescriptor{

        public static final String DISPLAY_NAME = "Built-in Field Matcher"

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
