package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.IssueField
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONObject

/**
 * @author ceilfors
 */
@SuppressWarnings('Instanceof')
class CustomFieldParameterResolver implements ParameterResolver {

    CustomFieldParameterMapping customFieldParameterMapping

    CustomFieldParameterResolver(CustomFieldParameterMapping customFieldParameterMapping) {
        this.customFieldParameterMapping = customFieldParameterMapping
    }

    String resolve(Issue issue) {
        String customFieldId = "customfield_${customFieldParameterMapping.customFieldId}"
        IssueField field = issue.fields.toList().find { f -> f.id == customFieldId }
        if (field) {
            extractValue(field)
        } else {
            throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE)
                .add('customFieldId', customFieldId)
        }
    }

    private static String extractValue(IssueField field) {
        Object fieldValue = field.value
        if (fieldValue instanceof JSONArray) {
            return toList(fieldValue).collect { extractSingleValue(it) }.join(', ')
        }
        extractSingleValue(fieldValue)
    }

    private static List toList(JSONArray jsonArray) {
        (0..jsonArray.length() - 1).collect { i -> jsonArray.get(i) }
    }

    @SuppressWarnings('DuplicateStringLiteral') // Clearer with String literals
    private static String extractSingleValue(Object singleValue) {
        if (singleValue == null) {
            return null
        } else if (singleValue instanceof String) {
            return singleValue
        } else if (singleValue instanceof Number) {
            return String.valueOf(singleValue)
        } else if (singleValue instanceof JSONObject) {
            JSONObject object = singleValue
            if (object.has('value')) {
                String value = object.getString('value')
                if (object.has('child')) {
                    value += " - ${object.getJSONObject('child').getString('value')}"
                }
                return value
            } else if (object.has('name')) {
                return object.getString('name')
            }
        }

        throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE)
                .add('customFieldValue', singleValue.dump())
    }
}
