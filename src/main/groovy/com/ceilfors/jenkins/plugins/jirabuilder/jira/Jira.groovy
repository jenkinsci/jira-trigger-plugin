package com.ceilfors.jenkins.plugins.jirabuilder.jira

/**
 * Adapter layer to communicate with JIRA. There are too many
 * ways of communicating with JIRA i.e. JRJC, rcarz/jira-client, plain REST, etc.
 *
 * @author ceilfors
 */
interface Jira {

    String createIssue()

    String createIssue(String description)

    void addComment(String issueKey, String comment)

    void registerWebhook(String url)

    def deleteAllWebhooks()

    Map getIssueMap(String issueKey)

    boolean validateIssueKey(String issueKey, String jqlFilter)
}
