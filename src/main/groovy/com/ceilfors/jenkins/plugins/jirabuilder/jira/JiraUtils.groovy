package com.ceilfors.jenkins.plugins.jirabuilder.jira

import com.atlassian.jira.rest.client.api.domain.Comment

/**
 * @author ceilfors
 */
class JiraUtils {

    static String getIssueIdFromComment(Comment comment) {
        (comment.self.toString() =~ ".*issue/(\\d+)/comment/\\d+.*")[0][1]
    }
}
