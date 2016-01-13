package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import com.atlassian.jira.rest.client.api.domain.Comment
import hudson.model.ParameterValue

/**
 * Responsible for providing a parameter value for Jenkins by digesting JIRA information.
 *
 * @author ceilfors
 */
interface ParameterResolver<M extends ParameterMapping, V extends ParameterValue > {

    V resolve(Comment comment, M parameterMapping)
}
