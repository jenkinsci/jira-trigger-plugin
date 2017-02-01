package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue

/**
 * @author ceilfors
 */
class TestUtils {

    static Comment createComment(String body) {
        new Comment(null, body, null, null, null, null, null, null)
    }

    static Issue createIssue(String issueKey) {
        new Issue(null, null, issueKey, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null)
    }
}
