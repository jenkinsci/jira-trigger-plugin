package com.ceilfors.jenkins.plugins.jiratrigger

import groovy.transform.PackageScope
import hudson.Extension
import hudson.util.Secret
import jenkins.model.GlobalConfiguration
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
/**
 * @author ceilfors
 */
@Extension
class JiraTriggerGlobalConfiguration extends GlobalConfiguration {

    private String jiraRootUrl
    private String jiraUsername
    private Secret jiraPassword
    private boolean jiraCommentReply = false

    JiraTriggerGlobalConfiguration() {
        load()
    }

    JiraTriggerGlobalConfiguration(String jiraRootUrl, String jiraUsername, String jiraPassword) {
        setJiraRootUrl(jiraRootUrl)
        setJiraUsername(jiraUsername)
        setJiraPassword(jiraPassword)
    }

    @Override
    boolean configure(StaplerRequest req, JSONObject formData) {
        setJiraRootUrl(formData.getString("jiraRootUrl"))
        setJiraUsername(formData.getString("jiraUsername"))
        setJiraPassword(formData.getString("jiraPassword"))
        jiraCommentReply = formData.getBoolean("jiraCommentReply")
        save();
        return super.configure(req, formData)
    }

    String getJiraRootUrl() {
        return jiraRootUrl
    }

    String getJiraUsername() {
        return jiraUsername
    }

    Secret getJiraPassword() {
        return jiraPassword
    }

    boolean getJiraCommentReply() {
        return jiraCommentReply
    }

    @PackageScope
    void setJiraRootUrl(String jiraRootUrl) {
        this.jiraRootUrl = jiraRootUrl
    }

    @PackageScope
    void setJiraUsername(String jiraUsername) {
        this.jiraUsername = jiraUsername
    }

    @PackageScope
    void setJiraPassword(String jiraPassword) {
        this.jiraPassword = Secret.fromString(jiraPassword)
    }

    @PackageScope
    void setJiraCommentReply(boolean jiraCommentReply) {
        this.jiraCommentReply = jiraCommentReply
    }

    void validateConfiguration() {
        if (!jiraRootUrl) {
            throw new JiraTriggerException(JiraTriggerErrorCode.JIRA_NOT_CONFIGURED).add("config", "jiraRootUrl")
        }
        if (!jiraPassword) {
            throw new JiraTriggerException(JiraTriggerErrorCode.JIRA_NOT_CONFIGURED).add("config", "jiraPassword")
        }
        if (!jiraUsername) {
            throw new JiraTriggerException(JiraTriggerErrorCode.JIRA_NOT_CONFIGURED).add("config", "jiraUsername")
        }
    }
}
