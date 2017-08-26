package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import groovy.transform.EqualsAndHashCode
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import hudson.model.StringParameterValue

/**
 * @author ceilfors
 */
@EqualsAndHashCode
abstract class ParameterMapping extends AbstractDescribableImpl<ParameterMapping> {

    final String jenkinsParameter

    protected ParameterMapping(String jenkinsParameter) {
        this.jenkinsParameter = jenkinsParameter.trim()
    }

    abstract ParameterResolver<StringParameterValue> getParameterResolver()

    static abstract class ParameterMappingDescriptor extends Descriptor<ParameterMapping> {

        @Override
        abstract String getDisplayName()
    }
}
