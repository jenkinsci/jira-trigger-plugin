package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import com.google.inject.Singleton
import hudson.model.StringParameterValue

/**
 * @author ceilfors
 */
@Singleton
class IssueAttributePathParameterResolver
        implements ParameterResolver<IssueAttributePathParameterMapping, StringParameterValue> {

    StringParameterValue resolve(Issue issue, IssueAttributePathParameterMapping issueAttributePathParameterMapping) {
        String attributeValue = resolveProperty(issue.properties, issueAttributePathParameterMapping.issueAttributePath)
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
            if (!property.contains('.') && !map.containsKey(property)) {
                // If property is not nested, Eval.x returns null instead of throwing NPE
                throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE)
            }
            Eval.x(map, 'x.' + property)
        } catch (MissingPropertyException e) {
            throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE, e)
        }
    }
}
