package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.ceilfors.jenkins.plugins.jiratrigger.jira.*
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhook
import jenkins.model.GlobalConfiguration
import org.junit.rules.ExternalResource
/**
 * @author ceilfors
 */
class JiraSetupRule extends ExternalResource {

    String jiraRootUrl = "http://localhost:2990/jira"
    String jiraUsername = "admin"
    String jiraPassword = "admin"
    JenkinsRunner jenkinsRunner

    JiraSetupRule(JenkinsRunner jenkinsRunner) {
        this.jenkinsRunner = jenkinsRunner
    }

    protected void before() throws Throwable {
        JiraTriggerGlobalConfiguration jiraTriggerGlobalConfiguration = jenkinsConfiguration()
        ExtendedJiraRestClient jiraRestClient = new JrjcJiraClient(jiraTriggerGlobalConfiguration).jiraRestClient
        webhookConfiguration(jiraRestClient)
    }

    def webhookConfiguration(ExtendedJiraRestClient jiraRestClient) {
        WebhookRestClient webhookRestClient = jiraRestClient.webhookRestClient
        Iterable<Webhook> webhooks = webhookRestClient.getWebhooks().claim()
        webhooks = webhooks.findAll { it.name.contains("Acceptance Test") || it.name.contains("Local Jenkins")}
        webhooks.each { webhook ->
            webhookRestClient.unregisterWebhook(webhook.selfUri).claim()
        }

        webhookRestClient.registerWebhook(new WebhookInput(name: "Acceptance Test", events: [JiraWebhook.WEBHOOK_EVENT], url: jenkinsRunner.webhookUrl)).claim()
        webhookRestClient.registerWebhook(new WebhookInput(name: "Acceptance Test (Vagrant)", events: [JiraWebhook.WEBHOOK_EVENT], url: jenkinsRunner.webhookUrl.replace("localhost", "10.0.2.2"))).claim()
        webhookRestClient.registerWebhook(new WebhookInput(name: "Local Jenkins", events: [JiraWebhook.WEBHOOK_EVENT], url: "http://localhost:8080/${jenkinsRunner.jiraWebhook.urlName}/")).claim()
    }

    JiraTriggerGlobalConfiguration jenkinsConfiguration() {
        JiraTriggerGlobalConfiguration configuration = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        configuration.jiraRootUrl = jiraRootUrl
        configuration.jiraUsername = jiraUsername
        configuration.jiraPassword = jiraPassword
        configuration.save()
        return configuration
    }
}
