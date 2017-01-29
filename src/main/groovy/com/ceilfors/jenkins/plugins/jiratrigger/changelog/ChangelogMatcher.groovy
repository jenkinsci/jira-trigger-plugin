package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.FieldType
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import hudson.Util
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import org.kohsuke.stapler.DataBoundSetter

/**
 * @author ceilfors
 */
@ToString(includeNames = true)
@EqualsAndHashCode
abstract class ChangelogMatcher extends AbstractDescribableImpl<ChangelogMatcher> {

    final FieldType fieldType
    final String field
    final String newValue
    final String oldValue
    boolean comparingNewValue = true
    boolean comparingOldValue = true

    protected ChangelogMatcher(FieldType fieldType, String field, String newValue, String oldValue) {
        this.fieldType = fieldType
        this.field = field
        this.newValue = newValue
        this.oldValue = oldValue
    }

    @DataBoundSetter
    void setComparingNewValue(boolean comparingNewValue) {
        this.comparingNewValue = comparingNewValue
    }

    @DataBoundSetter
    void setComparingOldValue(boolean comparingOldValue) {
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