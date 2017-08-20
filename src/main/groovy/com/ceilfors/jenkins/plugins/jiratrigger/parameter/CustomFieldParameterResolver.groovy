package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.IssueField
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import com.google.inject.Singleton
import hudson.model.StringParameterValue
import org.codehaus.jettison.json.JSONArray

/**
 * @author ceilfors
 */
@Singleton
class CustomFieldParameterResolver
        implements ParameterResolver<CustomFieldParameterMapping, StringParameterValue> {

    StringParameterValue resolve(Issue issue, CustomFieldParameterMapping customFieldParameterMapping) {
        String customFieldId = "customfield_${customFieldParameterMapping.customFieldId}"
        IssueField field = issue.fields.toList().find { f -> f.id == customFieldId }
        if (field) {
            new StringParameterValue(customFieldParameterMapping.jenkinsParameter, extractValue(field))
        } else {
            throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE)
        }
    }

    private static String extractValue(IssueField field) {
        if (field.value instanceof JSONArray) {
            JSONArray jsonArray = field.value
            return (0..jsonArray.length() - 1).collect { jsonArray.getString(it) }.join(', ')
        }

        field.value as String
    }
}
