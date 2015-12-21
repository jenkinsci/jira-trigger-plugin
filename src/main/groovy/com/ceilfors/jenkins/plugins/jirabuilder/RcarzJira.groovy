package com.ceilfors.jenkins.plugins.jirabuilder
import net.rcarz.jiraclient.BasicCredentials
import net.rcarz.jiraclient.Field
import net.rcarz.jiraclient.Issue
import net.rcarz.jiraclient.JiraClient
/**
 * @author ceilfors
 */
class RcarzJira implements Jira {

    private JiraClient jiraClient

    public RcarzJira() {
        BasicCredentials creds = new BasicCredentials("admin", "admin");
        jiraClient = new JiraClient("http://localhost:2990/jira", creds);
    }

    @Override
    String createIssue() {
        Issue issue = jiraClient.createIssue("TEST", "Task")
                .field(Field.SUMMARY, "task summary")
                .execute()
        return issue.key
    }

    @Override
    void addComment(String issueKey, String comment) {
        jiraClient.getIssue(issueKey).addComment(comment)
    }
}
