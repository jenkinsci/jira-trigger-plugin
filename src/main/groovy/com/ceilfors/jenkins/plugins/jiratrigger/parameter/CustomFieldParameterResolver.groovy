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
            return toList(fieldValue).collect { extractSingleValue(it) }.join(', ')
        }
        extractSingleValue(fieldValue)
    }

    private static toList(JSONArray jsonArray) {
        (0..jsonArray.length() - 1).collect { i -> jsonArray.get(i) }
    }

    private static String extractSingleValue(singleValue) {
        if (singleValue == null) {
            return null
        } else if (singleValue instanceof String) {
            return singleValue
        } else if (singleValue instanceof Number) {
            return String.valueOf(singleValue)
        } else if (singleValue instanceof JSONObject) {
            JSONObject object = singleValue
            String value = object.getString('value')
            if (object.has('child')) {
                value += " - ${object.getJSONObject('child').getString('value')}"
            }
            return value
        }

        throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE)
    }
}
