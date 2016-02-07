package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient

/**
 * @author ceilfors
 */
interface JiraRunner extends JiraClient {

    String createIssue()

    String createIssue(String description)

    void updateDescription(String issueKey, String description)

    void updateStatus(String issueKey, String status)

    void shouldBeNotifiedWithComment(String issueKey, String jobName)
}