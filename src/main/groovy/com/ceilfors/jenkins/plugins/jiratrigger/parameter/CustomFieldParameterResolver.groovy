package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.IssueField
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import com.google.inject.Singleton
import hudson.model.StringParameterValue
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

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
        Object fieldValue = field.value
        if (fieldValue instanceof JSONArray) {
            JSONArray array = fieldValue
            return (0..array.length() - 1).collect { array.getString(it) }.join(', ')
        } else if (fieldValue instanceof JSONObject) {
            JSONObject object = fieldValue
            String value = object.get('value')
            if (object.has('child')) {
                value += " - ${object.getJSONObject('child').get('value')}"
            }
            return value
        }

        fieldValue as String
    }
}
