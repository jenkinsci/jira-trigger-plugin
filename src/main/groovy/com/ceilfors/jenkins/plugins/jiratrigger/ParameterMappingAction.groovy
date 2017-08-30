package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterMapping
import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.EnvironmentContributingAction

/**
 * @author ceilfors
 */
class ParameterMappingAction implements EnvironmentContributingAction {

    private final Issue issue
    private final List<ParameterMapping> parameterMappings

    ParameterMappingAction(Issue issue, List<ParameterMapping> parameterMappings) {
        this.issue = issue
        this.parameterMappings = parameterMappings
    }

    @Override
    void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        parameterMappings.each { parameterMapping ->
            env.put(parameterMapping.jenkinsParameter,
                    parameterMapping.parameterResolver.resolve(issue).value as String)
        }
    }

    @Override
    String getIconFileName() {
        null
    }

    @Override
    String getDisplayName() {
        null
    }

    @Override
    String getUrlName() {
        null
    }
}
