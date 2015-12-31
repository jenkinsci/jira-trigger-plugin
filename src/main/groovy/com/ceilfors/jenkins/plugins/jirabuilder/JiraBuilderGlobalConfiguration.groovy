package com.ceilfors.jenkins.plugins.jirabuilder

import hudson.Extension
import jenkins.model.GlobalConfiguration
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
/**
 * @author ceilfors
 */
@Extension
class JiraBuilderGlobalConfiguration extends GlobalConfiguration {

    private String jiraRootUrl
    private String jiraUsername
    private String jiraPassword

    JiraBuilderGlobalConfiguration() {
        load()
    }

    JiraBuilderGlobalConfiguration(String jiraRootUrl, String jiraUsername, String jiraPassword) {
        this.jiraRootUrl = jiraRootUrl
        this.jiraUsername = jiraUsername
        this.jiraPassword = jiraPassword
    }

    @Override
    boolean configure(StaplerRequest req, JSONObject formData) {
        jiraRootUrl = formData.getString("jiraRootUrl")
        jiraUsername = formData.getString("jiraUsername")
        jiraPassword = formData.getString("jiraPassword")
        save();
        return super.configure(req, formData)
    }

    String getRootUrl() {
        return jiraRootUrl
    }

    String getUsername() {
        return jiraUsername
    }

    String getPassword() {
        return jiraPassword
    }
}
