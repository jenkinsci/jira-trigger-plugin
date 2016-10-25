package com.ceilfors.jenkins.plugins.jiratrigger

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

    String jiraRootUrl
    String jiraUsername
    private Secret jiraPassword
    boolean jiraCommentReply = false

    JiraTriggerGlobalConfiguration() {
        load()
    }

    JiraTriggerGlobalConfiguration(String jiraRootUrl, String jiraUsername, String jiraPassword) {
        this.jiraRootUrl = jiraRootUrl
        this.jiraUsername = jiraUsername
        this.setJiraPassword(jiraPassword)
    }

    @Override
    boolean configure(StaplerRequest req, JSONObject formData) {
        req.bindJSON(this, formData)
        save();
        return true;
    }

    Secret getJiraPassword() {
        return jiraPassword
    }

    void setJiraPassword(String jiraPassword) {
        this.jiraPassword = Secret.fromString(jiraPassword)
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
