package com.ceilfors.jenkins.plugins.jiratrigger
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JrjcJiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterResolver
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterResolver
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhookListener
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import hudson.Extension
/**
 * @author ceilfors
 */
@Extension
class JiraTriggerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JiraWebhookListener).to(JiraTriggerExecutor).in(Scopes.SINGLETON)
        bind(JiraClient).to(JrjcJiraClient).in(Scopes.SINGLETON)
        bind(ParameterResolver).to(IssueAttributePathParameterResolver).in(Scopes.SINGLETON)

        Multibinder<JiraTriggerListener> jiraTriggerListenerBinder = Multibinder.newSetBinder(binder(), JiraTriggerListener)
        jiraTriggerListenerBinder.addBinding().to(JiraBuildReplier)
    }
}
