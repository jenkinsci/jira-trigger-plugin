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

    @Unroll
    def 'Should be able to resolve #customFieldType custom field when the value is not empty'(
            String customFieldType, String customFieldId, String attributeValue) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('parameter', customFieldId)
        CustomFieldParameterResolver subject = new CustomFieldParameterResolver(mapping)
        StringParameterValue result = subject.resolve(createIssueFromFile('single_value_custom_field'))

        then:
        result != null
        result.value == attributeValue
        result.name == 'parameter'

        where:
        customFieldType                  | customFieldId | attributeValue
        'Checkboxes'                     | '10100'       | 'checkbox option 1'
        'Date Picker'                    | '10101'       | '2017-08-17'
        'Date Time Picker'               | '10102'       | '2017-08-17T01:00:00.000+0000'
        'Labels'                         | '10103'       | 'label'
        'Number Field'                   | '10104'       | '1.0'
        'Radio Buttons'                  | '10105'       | 'radio option 1'
        'Select List (multiple choices)' | '10107'       | 'singlelist option 1'
        'Select List (cascading)'        | '10106'       | 'cascade option 1'
        'Select List (single choice)'    | '10200'       | 'single choice option 1'
        'Text Field (multi-line)'        | '10000'       | 'barclays\r\nhalifax\r\nsantander'
        'Text Field (single line)'       | '10108'       | 'text'
        'User Picker (single user)'      | '10110'       | 'testusernameedited'
        'URL Field'                      | '10109'       | 'https://url.com'
    }

    @Unroll
    def 'Should be able to resolve #customFieldType custom field when it contains multiple values'(
            String customFieldType, String customFieldId, String attributeValue) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('parameter', customFieldId)
        CustomFieldParameterResolver subject = new CustomFieldParameterResolver(mapping)
        StringParameterValue result = subject.resolve(createIssueFromFile('multi_value_custom_field'))

        then:
        result != null
        result.value == attributeValue
        result.name == 'parameter'

        where:
        customFieldType                  | customFieldId | attributeValue
        'Checkboxes'                     | '10100'       | 'checkbox option 1, checkbox option 2'
        'Labels'                         | '10103'       | 'label, labela, labelb'
        'Select List (multiple choices)' | '10107'       | 'singlelist option 1, singlelist option 2'
        'Select List (cascading)'        | '10106'       | 'cascade option 1 - child 1 1'
    }

    @Unroll
    def 'Should be able to resolve #customFieldType custom field when the value is empty'(
            String customFieldType, String customFieldId, String attributeValue) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('parameter', customFieldId)
        CustomFieldParameterResolver subject = new CustomFieldParameterResolver(mapping)
        StringParameterValue result = subject.resolve(createIssueFromFile('empty_custom_field'))

        then:
        result != null
        result.value == attributeValue
        result.name == 'parameter'

        where:
        customFieldType                  | customFieldId | attributeValue
        'Checkboxes'                     | '10100'       | null
        'Date Picker'                    | '10101'       | null
        'Date Time Picker'               | '10102'       | null
        'Labels'                         | '10103'       | null
        'Number Field'                   | '10104'       | null
        'Radio Buttons'                  | '10105'       | null
        'Select List (multiple choices)' | '10107'       | null
        'Select List (cascading)'        | '10106'       | null
        'Select List (single choice)'    | '10200'       | null
        'Text Field (multi-line)'        | '10000'       | null
        'Text Field (single line)'       | '10108'       | null
        'User Picker (single user)'      | '10110'       | null
        'URL Field'                      | '10109'       | null
    }

    @Unroll
    def 'Should throw exception when parameter custom field id is not available'(String customFieldId) {
        when:
        CustomFieldParameterMapping mapping = new CustomFieldParameterMapping('uhused', customFieldId)
        CustomFieldParameterResolver subject = new CustomFieldParameterResolver(mapping)
        subject.resolve(createIssueFromFile('single_value_custom_field'))

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
