package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.IssueField
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import com.google.inject.Singleton
import hudson.model.StringParameterValue

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
            new StringParameterValue(customFieldParameterMapping.jenkinsParameter, field.value as String)
        } else {
            throw new JiraTriggerException(ParameterErrorCode.FAILED_TO_RESOLVE)
        }
    }
}
