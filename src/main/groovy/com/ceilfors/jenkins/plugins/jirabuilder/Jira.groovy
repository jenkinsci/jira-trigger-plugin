package com.ceilfors.jenkins.plugins.jirabuilder

/**
 * Adapter layer to communicate with JIRA. There are too many
 * ways of communicating with JIRA i.e. JRJC, rcarz/jira-client, plain REST, etc.
 *
 * @author ceilfors
 */
interface Jira {

    String createIssue()

    void addComment(String issueKey, String comment)

    void registerWebHook(String url)

    def deleteAllWebHooks()
}
