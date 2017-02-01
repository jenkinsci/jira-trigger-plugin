package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import hudson.model.StringParameterValue
import org.codehaus.jettison.json.JSONObject
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author ceilfors
 */
class IssueAttributePathParameterResolverTest extends Specification {

    private Issue createIssueFromFile(issueKey) {
        def issueJsonObject = new JSONObject(this.class.getResource("${issueKey}.json").text)
        return new IssueJsonParser(new JSONObject([:]), new JSONObject([:])).parse(issueJsonObject)
    }

    @Unroll
    def 'Should be able to resolve parameter by hitting JIRA'(String attributePath, String attributeValue) {
        given:
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver()

        when:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping('parameter', attributePath)
        StringParameterValue result = resolver.resolve(createIssueFromFile('TEST-136'), mapping)

        then:
        result != null
        result.value == attributeValue
        result.name == 'parameter'

        where:
        attributePath                          | attributeValue
        'key'                                  | 'TEST-136'
        'project.key'                          | 'TEST'
        'id'                                   | 11120
        'timeTracking.originalEstimateMinutes' | 5
        'status.name'                          | 'To Do'
        'summary'                              | 'summary content'
    }

    @Unroll
    def 'Should throw exception when parameter is not resolvable'(String attributePath) {
        given:
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver()

        when:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping('unused', attributePath)
        resolver.resolve(createIssueFromFile('TEST-136'), mapping)

        then:
        thrown JiraTriggerException

        where:
        //noinspection SpellCheckingInspection
        attributePath << [
                'timeTracking.originalEstimateSeconds',
                'typo'
        ]
    }
}
