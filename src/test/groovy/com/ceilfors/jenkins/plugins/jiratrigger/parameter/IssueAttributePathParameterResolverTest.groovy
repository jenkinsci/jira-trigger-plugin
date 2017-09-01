package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import org.codehaus.jettison.json.JSONObject
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author ceilfors
 */
class IssueAttributePathParameterResolverTest extends Specification {

    private Issue createIssueFromFile(issueKey) {
        def issueJsonObject = new JSONObject(this.class.getResource("${issueKey}.json").text)
        new IssueJsonParser(new JSONObject([:]), new JSONObject([:])).parse(issueJsonObject)
    }

    @Unroll
    def 'Should be able to resolve parameter by hitting JIRA'(String attributePath, String attributeValue) {
        given:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping('parameter', attributePath)
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver(mapping)

        when:
        String result = resolver.resolve(createIssueFromFile('TEST-136'))

        then:
        result == attributeValue

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
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping('unused', attributePath)
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver(mapping)

        when:
        resolver.resolve(createIssueFromFile('TEST-136'))

        then:
        thrown JiraTriggerException

        where:
        //noinspection SpellCheckingInspection
        attributePath << [
                'timeTracking.originalEstimateSeconds',
                'typo',
        ]
    }
}
