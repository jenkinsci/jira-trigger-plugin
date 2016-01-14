package com.ceilfors.jenkins.plugins.jirabuilder

import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient

/**
 * @author ceilfors
 */
interface JiraRunner extends JiraClient {

    void registerWebhook(String url)

    void deleteAllWebhooks()

    String createIssue()

    String createIssue(String description)

    void addComment(String issueKey, String comment)
}