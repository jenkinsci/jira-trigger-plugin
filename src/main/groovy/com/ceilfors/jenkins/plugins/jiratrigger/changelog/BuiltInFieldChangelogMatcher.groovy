package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import hudson.Extension
import org.kohsuke.stapler.DataBoundConstructor

/**
 * @author ceilfors
 */
class BuiltInFieldChangelogMatcher extends ChangelogMatcher {

    private final String field
    private final String newValue

    @DataBoundConstructor
    BuiltInFieldChangelogMatcher(String field, String newValue) {
        this.field = field
        this.newValue = newValue
    }

    String getField() {
        return field
    }

    String getNewValue() {
        return newValue
    }

    @SuppressWarnings("UnnecessaryQualifiedReference") // Can't remove qualifier, IntelliJ bug?
    @Extension
    static class BuiltInFieldChangelogMatcherDescriptor extends ChangelogMatcher.ChangelogMatcherDescriptor{

        @Override
        String getDisplayName() {
            "Built-in Field Matcher"
        }
    }
}
