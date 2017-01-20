package com.ceilfors.jenkins.plugins.jiratrigger.jira

import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import hudson.util.Secret
import jenkins.model.GlobalConfiguration
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

/**
 * @author ceilfors
 */
class JrjcJiraClientIntegrationTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    def 'Creates a new jira client cache when global configuration is updated'() {
        given:
        JiraTriggerGlobalConfiguration configuration = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        configuration.jiraRootUrl = 'http://localhost:2990/jira'
        configuration.jiraUsername = 'admin'
        configuration.jiraPassword = Secret.fromString('admin')
        configuration.save()

        JrjcJiraClient jiraClient = jenkins.jenkins.injector.getInstance(JrjcJiraClient)

        when:
        def originalJiraRestClient = jiraClient.jiraRestClient

        then:
        jiraClient.jiraRestClient.is(originalJiraRestClient)

        when:
        configuration.jiraUsername = 'test'
        configuration.save()

        then:
        !jiraClient.jiraRestClient.is(originalJiraRestClient)
    }
}
