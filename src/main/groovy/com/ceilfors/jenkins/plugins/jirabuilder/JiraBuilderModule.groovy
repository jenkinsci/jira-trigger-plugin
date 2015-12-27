package com.ceilfors.jenkins.plugins.jirabuilder

import com.ceilfors.jenkins.plugins.jirabuilder.jira.Jira
import com.ceilfors.jenkins.plugins.jirabuilder.jira.RcarzJira
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebHookListener
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
        bind(JiraWebHookListener).to(JiraBuilder).in(Scopes.SINGLETON)
        bind(Jira).to(RcarzJira).in(Scopes.SINGLETON)
        bind(Jenkins).toInstance(Jenkins.instance)
    }
}
