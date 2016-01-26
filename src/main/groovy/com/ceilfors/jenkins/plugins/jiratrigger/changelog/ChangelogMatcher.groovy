package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor

/**
 * @author ceilfors
 */
abstract class ChangelogMatcher extends AbstractDescribableImpl<ChangelogMatcher> {

    static abstract class ChangelogMatcherDescriptor extends Descriptor<ChangelogMatcher> {

        @Override
        String getDisplayName() {
            "Changelog matcher"
        }
    }
}