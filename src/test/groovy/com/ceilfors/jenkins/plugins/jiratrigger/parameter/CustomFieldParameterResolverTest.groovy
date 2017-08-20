package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookChangelogEventJsonParser
import hudson.model.StringParameterValue
import org.codehaus.jettison.json.JSONObject
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author ceilfors
 */
class CustomFieldParameterResolverTest extends Specification {

    private Issue createIssueFromFile(fileName) {
        def jsonObject = new JSONObject(this.class.getResource("${fileName}.json").text)
        new WebhookChangelogEventJsonParser().parse(jsonObject).issue
    }

    CustomFieldParameterResolver subject

    def setup() {
        subject = new CustomFieldParameterResolver()
    }

    @Unroll
    def 'Should be able to resolve #customFieldType custom field when the value is not empty'(
            String customFieldType, String customFieldId, String attributeValue) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('parameter', customFieldId)
        StringParameterValue result = subject.resolve(createIssueFromFile('single_value_custom_field'), mapping)

        then:
        result != null
        result.value == attributeValue
        result.name == 'parameter'

        where:
        customFieldType   | customFieldId | attributeValue
        'Free Text Field' | '10000'       | 'barclays'
        'Date Picker'     | '10101'       | '2017-08-17'
        'Date Time'       | '10102'       | '2017-08-17T01:00:00.000+0000'
        'Labels'          | '10103'       | 'label'
    }

    @Unroll
    def 'Should be able to resolve #customFieldType custom field when it contains multiple values'(
            String customFieldType, String customFieldId, String attributeValue) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('parameter', customFieldId)
        StringParameterValue result = subject.resolve(createIssueFromFile('multi_value_custom_field'), mapping)

        then:
        result != null
        result.value == attributeValue
        result.name == 'parameter'

        where:
        customFieldType   | customFieldId | attributeValue
        'Labels'          | '10103'       | 'label, labela, labelb'
    }

    @Unroll
    def 'Should be able to resolve #customFieldType custom field when the value is empty'(
            String customFieldType, String customFieldId, String attributeValue) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('parameter', customFieldId)
        StringParameterValue result = subject.resolve(createIssueFromFile('empty_custom_field'), mapping)

        then:
        result != null
        result.value == attributeValue
        result.name == 'parameter'

        where:
        customFieldType   | customFieldId | attributeValue
        'Free Text Field' | '10000'       | null
        'Date Picker'     | '10101'       | null
        'Date Time'       | '10102'       | null
        'Labels'          | '10103'       | null
    }

    @Unroll
    def 'Should throw exception when parameter custom field id is not available'(String customFieldId) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('unused', customFieldId)
        subject.resolve(createIssueFromFile('single_value_custom_field'), mapping)

        then:
        thrown JiraTriggerException

        where:
        //noinspection SpellCheckingInspection
        customFieldId << [
                '90000',
                'abc',
        ]
    }
}
