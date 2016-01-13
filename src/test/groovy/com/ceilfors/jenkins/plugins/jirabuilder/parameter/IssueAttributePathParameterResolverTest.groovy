package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import com.atlassian.jira.rest.client.api.domain.Comment
import com.ceilfors.jenkins.plugins.jirabuilder.JiraBuilderException
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import groovy.json.JsonSlurper
import hudson.model.StringParameterValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.ceilfors.jenkins.plugins.jirabuilder.TestUtils.createCommentWithIssueId

/**
 * @author ceilfors
 */
class IssueAttributePathParameterResolverTest extends Specification {

    private Map getIssueJson(issueId) {
        new JsonSlurper().parseText(this.class.getResource("${issueId}.json").text) as Map
    }

    @Unroll
    def "Should be able to resolve parameter by hitting JIRA"(String attributePath, String attributeValue) {
        given:
        JiraClient jiraClient = Mock(JiraClient)
        Comment comment = createCommentWithIssueId("1000")
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver(jiraClient)

        when:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping("parameter", attributePath)
        StringParameterValue result = resolver.resolve(comment, mapping)

        then:
        result != null
        result.value == attributeValue
        result.name == "parameter"
        1 * jiraClient.getIssueMap("1000") >> { getIssueJson("1000") }

        where:
        attributePath                 | attributeValue
        "fields.description"          | "description body"
        "fields.summary"              | "summary content"
        "fields.status.name"          | "To Do"
        "id"                          | "11120"
        "fields.timeoriginalestimate" | 300
    }

    @Unroll
    def "Should throw exception when parameter is not resolvable"(String attributePath) {
        given:
        JiraClient jiraClient = Mock(JiraClient)
        Comment comment = createCommentWithIssueId("1000")
        jiraClient.getIssueMap("1000") >> { getIssueJson("1000") }
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver(jiraClient)

        when:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping("unused", attributePath)
        resolver.resolve(comment, mapping)

        then:
        thrown JiraBuilderException

        where:
        //noinspection SpellCheckingInspection
        attributePath << [
                "fieldsa.description",
                "typo"
        ]

    }
}
