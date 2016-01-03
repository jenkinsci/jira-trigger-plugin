package com.ceilfors.jenkins.plugins.jirabuilder.jira

import com.atlassian.jira.rest.client.api.domain.Comment
import spock.lang.Specification

/**
 * @author ceilfors
 */
class JiraUtilsTest extends Specification {

    def "Should be able to get issue id from Comment object"(String selfString, String issueId) {
        given:
        Comment comment = new Comment(selfString.toURI(), null, null, null, null, null, null, null)

        when:
        def result = JiraUtils.getIssueIdFromComment(comment)

        then:
        result == issueId

        where:
        selfString                                                              | issueId
        "http://localhost:2990/jira/rest/api/2/issue/10003/comment/10000"       | "10003"
        "http://localhost:2990/jira/rest/api/2/issue/1/comment/10000"           | "1"
        "http://localhost:2990/jira/rest/api/2/issue/12341234567/comment/10000" | "12341234567"
    }
}
