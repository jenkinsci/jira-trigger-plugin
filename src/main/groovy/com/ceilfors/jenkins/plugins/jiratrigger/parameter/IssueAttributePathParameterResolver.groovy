package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Comment
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraUtils
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

    StringParameterValue resolve(Comment comment, IssueAttributePathParameterMapping issueAttributePathParameterMapping) {
        // KLUDGE: Hits JIRA multiple times, might want to handle multiple parameters at one time
        def issueMap = jiraClient.getIssueMap(JiraUtils.getIssueIdFromComment(comment).toString())
        String attributeValue = resolveProperty(issueMap, issueAttributePathParameterMapping.issueAttributePath)
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
                throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE)
            }
            Eval.x(map, 'x.' + property)
        } catch (NullPointerException e) {
            throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE, e)
        }
    }
}
