package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.ceilfors.jenkins.plugins.jiratrigger.jira.ExtendedJiraRestClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JrjcJiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.Webhook
import com.ceilfors.jenkins.plugins.jiratrigger.jira.WebhookInput
import com.ceilfors.jenkins.plugins.jiratrigger.jira.WebhookRestClient
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhook
import groovyx.net.http.HTTPBuilder
import hudson.util.Secret
import jenkins.model.GlobalConfiguration
import org.junit.rules.ExternalResource

/**
 * @author ceilfors
 */
class RealJiraSetupRule extends ExternalResource {

    public static final String CUSTOM_FIELD_NAME = 'My Customer Custom Field'
    public static String CUSTOM_FIELD_ID

    String jiraRootUrl = 'http://localhost:2990/jira'
    String jiraUsername = 'admin'
    String jiraPassword = 'admin'
    JenkinsRunner jenkinsRunner

    RealJiraSetupRule(JenkinsRunner jenkinsRunner) {
        this.jenkinsRunner = jenkinsRunner
    }

    protected void before() throws Throwable {
        configureJenkinsWithNormalUser()

        JiraTriggerGlobalConfiguration configuration = new JiraTriggerGlobalConfiguration(jiraRootUrl, 'admin', 'admin')
        ExtendedJiraRestClient jiraRestClient = new JrjcJiraClient(configuration).jiraRestClient
        configureWebhook(jiraRestClient.webhookRestClient)
        configureCustomField()
    }

    /**
     * This method hits JIRA Test Kit plugin REST API as the official REST API doesn't
     * support listing screen ids. The Test Kit client is however not used due to dependency hell.
     * There are a lot of transitive dependencies
     * that are dependent on by the test kit but not being declared explicitly. Trying to pull them manually seems
     * almost pull the entire Atlassian SDK which is huge. Because of the issue, http builder is
     * used instead.
     */
    private configureCustomField() {
        def http = new HTTPBuilder(jiraRootUrl + '/rest/testkit-test/1.0/')
        http.auth.basic 'admin', 'admin'

        def customFieldAlreadyAdded = false
        http.get(path: 'customFields/get') { resp, json ->
            def customField = json.find { it.name == CUSTOM_FIELD_NAME }
            if (customField) {
                CUSTOM_FIELD_ID = customField.id
                customFieldAlreadyAdded = true
            } else {
                customFieldAlreadyAdded = false
            }
        }
        if (!customFieldAlreadyAdded) {
            http.post(path: 'customFields/create', requestContentType: 'application/json', body: [
                    name       : CUSTOM_FIELD_NAME,
                    description: 'A custom field that contains customer name',
                    type       : 'com.atlassian.jira.plugin.system.customfieldtypes:textarea'
            ]) { resp, json ->
                CUSTOM_FIELD_ID = json.id
            }
        }

        List<String> screensWithoutCustomField = []
        http.get(path: 'screens') { resp, screens ->
            screens.each { screen ->
                boolean fieldAlreadyAdded = screen.tabs.find { tab -> tab.fields.find {it.name == CUSTOM_FIELD_NAME } }
                if (!fieldAlreadyAdded) {
                    screensWithoutCustomField.add(screen.name)
                }
            }

        }

        for (String screen : screensWithoutCustomField) {
            http.get(path: 'screens/addField', query: [screen: screen, field: CUSTOM_FIELD_NAME])
        }
    }

    def configureWebhook(WebhookRestClient webhookRestClient) {
        Iterable<Webhook> webhooks = webhookRestClient.getWebhooks().claim()
        webhooks = webhooks.findAll { it.name.contains('gradlew') }
        webhooks.each { webhook ->
            webhookRestClient.unregisterWebhook(webhook.selfUri).claim()
        }

        String vagrantHostDefaultIp = '10.0.2.2'
        [
                [name: 'Acceptance Test (gradlew test)',
                 url : jenkinsRunner.webhookUrl],
                [name: 'Acceptance Test (gradlew test) (Vagrant)',
                 url : jenkinsRunner.webhookUrl.replace('localhost', vagrantHostDefaultIp)],
                [name: 'Local Jenkins (gradlew server)',
                 url : "http://localhost:8080/${jenkinsRunner.jiraWebhook.urlName}/"],
                [name: 'Local Jenkins (gradlew server) (Vagrant)',
                 url : "http://${vagrantHostDefaultIp}:8080/${jenkinsRunner.jiraWebhook.urlName}/"],
        ].each {
            webhookRestClient.registerWebhook(
                    new WebhookInput(name: it.name, events: [JiraWebhook.WEBHOOK_EVENT], url: it.url)).claim()
        }
    }

    def configureJenkinsWithNormalUser() {
        JiraTriggerGlobalConfiguration configuration = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        configuration.jiraRootUrl = jiraRootUrl
        configuration.jiraUsername = jiraUsername
        configuration.jiraPassword = Secret.fromString(jiraPassword)
        configuration.save()
    }
}
