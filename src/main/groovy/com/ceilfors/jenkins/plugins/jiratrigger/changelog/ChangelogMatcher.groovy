package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
/**
 * @author ceilfors
 */
abstract class ChangelogMatcher extends AbstractDescribableImpl<ChangelogMatcher> {

    abstract boolean matches(ChangelogGroup changelogGroup)

    static abstract class ChangelogMatcherDescriptor extends Descriptor<ChangelogMatcher> {

        @Override
        String getDisplayName() {
            "Changelog matcher"
        }
    }
}