package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.FieldType
import hudson.Util
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor

/**
 * @author ceilfors
 */
class ChangelogMatcher extends AbstractDescribableImpl<ChangelogMatcher> {

    final FieldType fieldType
    final String field
    final String newValue
    final String oldValue
    final boolean comparingNewValue
    final boolean comparingOldValue

    ChangelogMatcher(FieldType fieldType, String field, String newValue, String oldValue,
                     boolean comparingNewValue, boolean comparingOldValue) {
        this.fieldType = fieldType
        this.field = field
        this.newValue = newValue
        this.oldValue = oldValue
        this.comparingNewValue = comparingNewValue
        this.comparingOldValue = comparingOldValue
    }

    boolean matches(ChangelogGroup changelogGroup) {
        changelogGroup.items.find {
            it.fieldType == fieldType &&
                    it.field == field &&
                    (comparingNewValue ? Util.fixNull(it.toString as String).equalsIgnoreCase(newValue) : true) &&
                    (comparingOldValue ? Util.fixNull(it.fromString as String).equalsIgnoreCase(oldValue) : true)
        }
    }

    static abstract class ChangelogMatcherDescriptor extends Descriptor<ChangelogMatcher> {
    }
}