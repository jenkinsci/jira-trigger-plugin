package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterMapping
import hudson.model.ParametersAction
import hudson.model.StringParameterValue

/**
 * @author ceilfors
 */
class ParameterMappingAction extends ParametersAction {

    ParameterMappingAction(Issue issue, List<ParameterMapping> parameterMappings) {
        super(parameterMappings.collect { p ->
            new StringParameterValue(p.jenkinsParameter, p.parameterResolver.resolve(issue))
        }, parameterMappings*.jenkinsParameter)
    }
}
