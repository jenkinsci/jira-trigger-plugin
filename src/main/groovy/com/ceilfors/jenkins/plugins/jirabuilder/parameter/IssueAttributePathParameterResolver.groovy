package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import com.ceilfors.jenkins.plugins.jirabuilder.JiraBuilderException
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import com.google.inject.Singleton
import hudson.model.StringParameterValue

import javax.inject.Inject

/**
 * @author ceilfors
 */
@Singleton
class IssueAttributePathParameterResolver implements ParameterResolver<IssueAttributePathParameterMapping, StringParameterValue> {

    private JiraClient jiraClient

    @Inject
    IssueAttributePathParameterResolver(JiraClient jiraClient) {
        this.jiraClient = jiraClient
    }

    StringParameterValue resolve(IssueAttributePathParameterMapping issueAttributePathParameterMapping, String issueKey) {
        String attributeValue = resolveProperty(jiraClient.getIssueMap(issueKey), issueAttributePathParameterMapping.issueAttributePath)
        new StringParameterValue(issueAttributePathParameterMapping.jenkinsParameter, attributeValue)
    }

    /**
     * Resolves nested property from a Map.
     *
     * @param map the map which property to be resolved
     * @param property
     * @return the resolved property, null otherwise
     */
    static String resolveProperty(Map map, String property) {
        try {
            if (!property.contains(".") && !map.containsKey(property)) {
                // If property is not nested, Eval.x returns null instead of throwing NPE
                throw new JiraBuilderException(ParameterErrorCode.FAILED_TO_RESOLVE)
            }
            Eval.x(map, 'x.' + property)
        } catch (NullPointerException e) {
            throw new JiraBuilderException(ParameterErrorCode.FAILED_TO_RESOLVE, e)
        }
    }
}
