package com.ceilfors.jenkins.plugins.jirabuilder
import com.ceilfors.jenkins.plugins.jirabuilder.jira.Jira
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JrjcJiraClient
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhookListener
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import hudson.Extension
import jenkins.model.Jenkins
/**
 * @author ceilfors
 */
@Extension
class JiraBuilderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JiraWebhookListener).to(JiraBuilder).in(Scopes.SINGLETON)
        bind(Jira).to(JrjcJiraClient).in(Scopes.SINGLETON)
        bind(Jenkins).toInstance(Jenkins.instance)
//        bind(JiraBuilderGlobalConfiguration).toInstance(GlobalConfiguration.all().get(JiraBuilderGlobalConfiguration))
    }
}
