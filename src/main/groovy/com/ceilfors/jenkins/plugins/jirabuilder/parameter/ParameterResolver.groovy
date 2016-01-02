package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import hudson.model.ParameterValue

/**
 * @author ceilfors
 */
interface ParameterResolver<M extends ParameterMapping, V extends ParameterValue > {

    V resolve(M parameterMapping, String issueKey)
}
