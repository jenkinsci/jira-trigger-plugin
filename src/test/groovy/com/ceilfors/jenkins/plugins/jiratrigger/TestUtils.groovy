package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Comment

/**
 * @author ceilfors
 */
class TestUtils {

    static Comment createCommentWithIssueId(String issueId) {
        URI commentUri = "http://localhost:2990/jira/rest/api/2/issue/${issueId}/comment/10000".toURI()
        new Comment(commentUri, null, null, null, null, null, null, null)
    }
}
