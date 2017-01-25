package com.ceilfors.jenkins.plugins.jiratrigger.jira

import com.atlassian.jira.rest.client.api.domain.Comment

/**
 * @author ceilfors
 */
class JiraUtils {

    static Long getIssueIdFromComment(Comment comment) {
        ((comment.self.toString() =~ '.*issue/(\\d+)/comment/\\d+.*')[0][1]) as Long
    }
}
