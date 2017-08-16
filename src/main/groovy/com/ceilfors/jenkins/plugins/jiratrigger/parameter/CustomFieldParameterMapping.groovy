package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import groovy.transform.EqualsAndHashCode
import hudson.Extension
import org.kohsuke.stapler.DataBoundConstructor

/**
 * @author ceilfors
 */
@EqualsAndHashCode(callSuper = true)
class CustomFieldParameterMapping extends ParameterMapping {

    final String customFieldId

    @DataBoundConstructor
    CustomFieldParameterMapping(String jenkinsParameter, String customFieldId) {
        super(jenkinsParameter)
        this.customFieldId = customFieldId.trim()
    }

    @SuppressWarnings('UnnecessaryQualifiedReference') // Can't remove qualifier, IntelliJ bug?
    @Extension
    static class CustomFieldParameterMappingDescriptor extends ParameterMapping.ParameterMappingDescriptor {

        public static final String DISPLAY_NAME = 'Custom Field'

        @Override
        String getDisplayName() {
            DISPLAY_NAME
        }
    }
}
