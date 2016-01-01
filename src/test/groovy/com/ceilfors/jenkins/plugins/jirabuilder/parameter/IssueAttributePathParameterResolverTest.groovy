package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import com.ceilfors.jenkins.plugins.jirabuilder.JiraBuilderException
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author ceilfors
 */
class IssueAttributePathParameterResolverTest extends Specification {

    private Map getIssueJson(issueKey) {
        new JsonSlurper().parseText(this.class.getResource("${issueKey}.json").text) as Map
    }

    @Unroll
    def "Should be able to resolve parameter by hitting JIRA"(String attributePath, String attributeValue) {
        given:
        JiraClient jiraClient = Mock(JiraClient)
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver(jiraClient)

        when:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping("unused", attributePath)
        String result = resolver.resolve(mapping, "TEST-136")

        then:
        result == attributeValue
        1 * jiraClient.getIssueMap("TEST-136") >> { getIssueJson("TEST-136") }

        where:
        attributePath        | attributeValue
        "fields.description" | "description body"
        "fields.summary"     | "summary content"
        "fields.status.name" | "To Do"
        "id"                 | "11120"
    }

    @Unroll
    def "Should throw exception when parameter is not resolvable"(String attributePath) {
        given:
        JiraClient jiraClient = Mock(JiraClient)
        jiraClient.getIssueMap("TEST-136") >> { getIssueJson("TEST-136") }
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver(jiraClient)

        when:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping("unused", attributePath)
        resolver.resolve(mapping, "TEST-136")

        then:
        thrown JiraBuilderException

        where:
        attributePath << [
                "fieldsa.description",
                "typo"
        ]

    }
}
