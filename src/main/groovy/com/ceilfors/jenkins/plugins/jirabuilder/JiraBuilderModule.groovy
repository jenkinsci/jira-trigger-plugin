package com.ceilfors.jenkins.plugins.jirabuilder
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JrjcJiraClient
import com.ceilfors.jenkins.plugins.jirabuilder.parameter.IssueAttributePathParameterResolver
import com.ceilfors.jenkins.plugins.jirabuilder.parameter.ParameterResolver
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhookListener
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import hudson.Extension
/**
 * @author ceilfors
 */
@Extension
class JiraBuilderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JiraWebhookListener).to(JiraBuilder).in(Scopes.SINGLETON)
        bind(JiraClient).to(JrjcJiraClient).in(Scopes.SINGLETON)
        bind(ParameterResolver).to(IssueAttributePathParameterResolver).in(Scopes.SINGLETON)

        Multibinder<JiraBuilderListener> jiraBuilderListenerBinder = Multibinder.newSetBinder(binder(), JiraBuilderListener);
        jiraBuilderListenerBinder.addBinding().to(CommentingJiraBuilderListener);
    }
}
