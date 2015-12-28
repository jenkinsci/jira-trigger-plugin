package com.ceilfors.jenkins.plugins.jirabuilder.parameter
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
/**
 * @author ceilfors
 */
abstract class ParameterMapping extends AbstractDescribableImpl<ParameterMapping> {

    private String jenkinsParameter

    ParameterMapping(String jenkinsParameter) {
        this.jenkinsParameter = jenkinsParameter
    }

    String getJenkinsParameter() {
        return jenkinsParameter
    }

    static abstract class ParameterMappingDescriptor extends Descriptor<ParameterMapping> {

        @Override
        String getDisplayName() {
            "Parameter mapping"
        }
    }
}
