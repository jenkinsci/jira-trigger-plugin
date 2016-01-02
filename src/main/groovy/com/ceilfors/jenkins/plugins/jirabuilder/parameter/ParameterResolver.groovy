package com.ceilfors.jenkins.plugins.jirabuilder.parameter

/**
 * @author ceilfors
 */
interface ParameterResolver<T extends ParameterMapping, R> {

    R resolve(T parameterMapping, String issueKey)
}
