package com.ceilfors.jenkins.plugins.jirabuilder.jira

/**
 * Adapter layer to communicate with JIRA. There are too many
 * ways of communicating with JIRA i.e. JRJC, rcarz/jira-client, plain REST, etc.
 *
 * @author ceilfors
 */
interface JiraClient {

    String createIssue()

    String createIssue(String description)

    void addComment(String issueKey, String comment)

    void registerWebhook(String url)

    def deleteAllWebhooks()

    Map getIssueMap(String issueKeyOrId)

    boolean validateIssueId(String issueId, String jqlFilter)
}
