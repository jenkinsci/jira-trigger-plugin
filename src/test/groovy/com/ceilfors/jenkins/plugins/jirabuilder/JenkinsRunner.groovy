package com.ceilfors.jenkins.plugins.jirabuilder

import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.FreeStyleProject
import hudson.model.ParametersAction
import hudson.model.ParametersDefinitionProperty
import hudson.model.StringParameterDefinition
import org.jvnet.hudson.test.JenkinsRule

import java.util.concurrent.TimeUnit

import static org.junit.Assert.assertThat
import static org.hamcrest.Matchers.*

/**
 * @author ceilfors
 */
class JenkinsRunner extends JenkinsRule {

    AbstractBuild buildShouldBeScheduled(String jobName) {
        def build = jiraBuilderAction.getLastScheduledBuild(15, TimeUnit.SECONDS)
        assertThat("Last scheduled build should be for the job matched", build.project.name, is(jobName))
        return build
    }

    private JiraWebHook getJiraBuilderAction() {
        instance.getActions().find { it instanceof JiraWebHook } as JiraWebHook
    }

    String getWebHookUrl() {
        return "${getURL().toString()}${jiraBuilderAction.urlName}/"
                .replace("localhost", "10.0.2.2") // vagrant
    }

    FreeStyleProject createFreeStyleProjectWithParameter(String name, String... parameters) {
        FreeStyleProject project = createFreeStyleProject(name)
        project.addProperty(new ParametersDefinitionProperty(parameters.collect {new StringParameterDefinition(it, "")}))
        return project
    }

    boolean buildTriggeredWithParameter(String jobName, String description) {
        def parametersAction = instance.getItemByFullName(jobName, AbstractProject).lastSuccessfulBuild.getAction(ParametersAction)
        assertThat(parametersAction.getParameter("description").value as String, is(description))
        return true
    }
}
