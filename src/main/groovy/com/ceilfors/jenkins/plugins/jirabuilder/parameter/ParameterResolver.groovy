package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import com.atlassian.jira.rest.client.api.domain.Comment
import hudson.model.ParameterValue

/**
 * @author ceilfors
 */
interface ParameterResolver<M extends ParameterMapping, V extends ParameterValue > {

    V resolve(Comment comment, M parameterMapping)
}
