package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
/**
 * @author ceilfors
 */
abstract class ParameterMapping extends AbstractDescribableImpl<ParameterMapping> {

    final String jenkinsParameter

    ParameterMapping(String jenkinsParameter) {
        this.jenkinsParameter = jenkinsParameter.trim()
    }

    static abstract class ParameterMappingDescriptor extends Descriptor<ParameterMapping> {

        @Override
        String getDisplayName() {
            "Parameter mapping"
        }
    }
}
