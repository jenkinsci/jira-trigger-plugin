package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import hudson.model.ParameterValue

/**
 * Responsible for providing a parameter value for Jenkins by digesting JIRA information.
 *
 * @author ceilfors
 */
interface ParameterResolver<M extends ParameterMapping, V extends ParameterValue > {

    V resolve(Issue issue, M parameterMapping)
}
