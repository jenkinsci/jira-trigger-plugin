package com.ceilfors.jenkins.plugins.jiratrigger.jira

/**
 * Adapter layer to communicate with JIRA. There are too many
 * ways of communicating with JIRA i.e. JRJC, rcarz/jira-client, plain REST, etc.
 *
 * @author ceilfors
 */
interface JiraClient {

    void addComment(String issueKey, String comment)

    Map getIssueMap(String issueKeyOrId)

    boolean validateIssueId(String issueId, String jqlFilter)
}
