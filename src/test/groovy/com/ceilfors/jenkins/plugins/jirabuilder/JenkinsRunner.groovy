package com.ceilfors.jenkins.plugins.jirabuilder

import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhook
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.*
import jenkins.model.GlobalConfiguration
import org.jvnet.hudson.test.JenkinsRule

import java.util.concurrent.TimeUnit

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
class JenkinsRunner extends JenkinsRule {

    AbstractBuild buildShouldBeScheduled(String jobName) {
        def build = jiraBuilder.getLastScheduledBuild(5, TimeUnit.SECONDS)
        assertThat("Build is not scheduled!", build, is(not(nullValue())))
        assertThat("Last build scheduled doesn't match the job name asserted", build.project.name, is(jobName))
        return build
    }

    void buildShouldNotBeScheduled(String jobName) {
        def build = jiraBuilder.getLastScheduledBuild(5, TimeUnit.SECONDS)
        assertThat("Build is scheduled", build, is(nullValue()))
    }

    private JiraBuilder getJiraBuilder() {
        instance.getInjector().getInstance(JiraBuilder)
    }

    private JiraWebhook getJiraWebhook() {
        instance.getActions().find { it instanceof JiraWebhook } as JiraWebhook
    }

    String getWebhookUrl() {
        return "${getURL().toString()}${jiraWebhook.urlName}/"
                .replace("localhost", "10.0.2.2") // vagrant
    }

    FreeStyleProject createJiraTriggeredProject(String name, String... parameters) {
        FreeStyleProject project = createFreeStyleProject(name)
        project.addProperty(new ParametersDefinitionProperty(parameters.collect {
            new StringParameterDefinition(it, "")
        }))

        JiraBuilderConfigurationPage configPage = configure(name)
        configPage.activateJiraBuilderTrigger()
        configPage.save()

        assertThat(project.triggers.values(), hasItem(instanceOf(JiraBuilderTrigger)))
        return project
    }

    boolean buildTriggeredWithParameter(String jobName, Map<String, String> parameterMap) {
        def parametersAction = instance.getItemByFullName(jobName, AbstractProject).lastSuccessfulBuild.getAction(ParametersAction)
        assertThat(parametersAction.parameters, containsInAnyOrder(*parameterMap.collect { key, value -> new StringParameterValue(key, value) }))
        return true
    }

    def setJiraBuilderCommentPattern(String name, String commentPattern) {
        JiraBuilderConfigurationPage configPage = configure(name)
        configPage.setCommentPattern(commentPattern)
        configPage.save()

        JiraBuilderTrigger jiraBuilderTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraBuilderTrigger)
        assertThat(jiraBuilderTrigger.commentPattern, is(commentPattern))
    }


    void setJiraBuilderJqlFilter(String name, String jqlFilter) {
        JiraBuilderConfigurationPage configPage = configure(name)
        configPage.setJqlFilter(jqlFilter)
        configPage.save()

        JiraBuilderTrigger jiraBuilderTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraBuilderTrigger)
        assertThat(jiraBuilderTrigger.jqlFilter, is(jqlFilter))
    }

    JiraBuilderConfigurationPage configure(String jobName) {
        HtmlPage htmlPage = createWebClient().goTo("job/$jobName/configure")
        return new JiraBuilderConfigurationPage(htmlPage)
    }

    JiraBuilderGlobalConfigurationPage globalConfigure() {
        HtmlPage htmlPage = createWebClient().goTo("configure")
        return new JiraBuilderGlobalConfigurationPage(htmlPage)
    }

    void addParameterMapping(String name, String jenkinsParameter, String issueAttributePath) {
        JiraBuilderTrigger jiraBuilderTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraBuilderTrigger)
        def originalParameterMappingSize = jiraBuilderTrigger.parameterMappings.size()

        JiraBuilderConfigurationPage configPage = configure(name)
        configPage.addParameterMapping(jenkinsParameter, issueAttributePath)
        configPage.save()

        jiraBuilderTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraBuilderTrigger)
        assertThat("Parameter mapping is not added", jiraBuilderTrigger.parameterMappings.size(), equalTo(originalParameterMappingSize + 1))
        assertThat(jiraBuilderTrigger.parameterMappings.last().jenkinsParameter, is(jenkinsParameter))
        assertThat(jiraBuilderTrigger.parameterMappings.last().issueAttributePath, is(issueAttributePath))
    }

    void setJiraBuilderGlobalConfig(String rootUrl, String username, String password) {
        JiraBuilderGlobalConfigurationPage configPage = globalConfigure()
        configPage.setRootUrl(rootUrl)
        configPage.setCredentials(username, password)
        configPage.save()

        def globalConfig = GlobalConfiguration.all().get(JiraBuilderGlobalConfiguration)
        assertThat(globalConfig.rootUrl, equalTo(rootUrl))
        assertThat(globalConfig.username, equalTo(username))
        assertThat(globalConfig.password, equalTo(password))
    }
}
