package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerException
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import groovy.json.JsonSlurper
import hudson.model.StringParameterValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.ceilfors.jenkins.plugins.jiratrigger.TestUtils.createIssue
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
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping("parameter", attributePath)
        StringParameterValue result = resolver.resolve(createIssue("TEST-136"), null, mapping)

        then:
        result != null
        result.value == attributeValue
        result.name == "parameter"
        1 * jiraClient.getIssueMap("TEST-136") >> { getIssueJson("TEST-136") }

        where:
        attributePath                 | attributeValue
        "fields.description"          | "description body"
        "fields.summary"              | "summary content"
        "fields.status.name"          | "To Do"
        "id"                          | 11120
        "fields.timeoriginalestimate" | 300
    }

    @Unroll
    def "Should throw exception when parameter is not resolvable"(String attributePath) {
        given:
        JiraClient jiraClient = Mock(JiraClient)
        jiraClient.getIssueMap("TEST-136") >> { getIssueJson("TEST-136") }
        IssueAttributePathParameterResolver resolver = new IssueAttributePathParameterResolver(jiraClient)

        when:
        IssueAttributePathParameterMapping mapping = new IssueAttributePathParameterMapping("unused", attributePath)
        resolver.resolve(createIssue("TEST-136"), null, mapping)

        then:
        thrown JiraTriggerException

        where:
        //noinspection SpellCheckingInspection
        attributePath << [
                "fieldsa.description",
                "typo"
        ]

    }
}
