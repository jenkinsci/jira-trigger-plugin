package com.ceilfors.jenkins.plugins.jirabuilder.webhook

/**
 * @author ceilfors
 */
class JiraWebHookContext {

    private final String issueKey
    private final Map eventBody
    private String userId
    private String userKey

    JiraWebHookContext(String issueKey, Map eventBody) {
        this.issueKey = issueKey
        this.eventBody = eventBody
    }

    void setUserId(String userId) {
        this.userId = userId
    }

    String getUserId() {
        return userId
    }

    void setUserKey(String userKey) {
        this.userKey = userKey
    }

    String getUserKey() {
        return userKey
    }

    String getIssueKey() {
        return issueKey
    }

    Map getEventBody() {
        return eventBody
    }
}
