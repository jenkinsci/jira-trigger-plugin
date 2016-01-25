package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient

/**
 * @author ceilfors
 */
interface JiraRunner extends JiraClient {

    void registerWebhook(String url)

    void deleteAllWebhooks()

    String createIssue()

    String createIssue(String description)

    void updateDescription(String issueKey, String description)

    void updateStatus(String issueKey, String status)

    void shouldBeNotifiedWithComment(String issueKey, String jobName)
}