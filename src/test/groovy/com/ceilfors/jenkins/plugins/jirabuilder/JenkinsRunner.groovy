package com.ceilfors.jenkins.plugins.jirabuilder
import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput
import com.gargoylesoftware.htmlunit.html.HtmlForm
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.*
import org.jvnet.hudson.test.JenkinsRule

import java.util.concurrent.TimeUnit

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat
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

    FreeStyleProject createJiraTriggeredProject(String name, String... parameters) {
        FreeStyleProject project = createFreeStyleProject(name)
        project.addProperty(new ParametersDefinitionProperty(parameters.collect {
            new StringParameterDefinition(it, "")
        }))

        HtmlPage configPage = this.createWebClient().goTo("job/$project.name/configure")
        HtmlCheckBoxInput triggerCheckBox = configPage.getFirstByXPath('//input[contains(@name, "JiraBuilderTrigger")]')
        triggerCheckBox.setChecked(true)

        HtmlForm form = configPage.getFormByName("config")
        form.submit((HtmlButton) last(form.getHtmlElementsByTagName("button")))

        assertThat(project.triggers.values(), hasItem(instanceOf(JiraBuilderTrigger)))
        return project
    }

    boolean buildTriggeredWithParameter(String jobName, Map<String, String> parameterMap) {
        def parametersAction = instance.getItemByFullName(jobName, AbstractProject).lastSuccessfulBuild.getAction(ParametersAction)
        parameterMap.each { key, value ->
            assertThat(parametersAction.getParameter(key).value as String, is(value))
        }
        return true
    }

    def setJiraBuilderCommentFilter(String name, String commentPattern) {
        HtmlPage configPage = this.createWebClient().goTo("job/$name/configure")

        HtmlForm form = configPage.getFormByName("config")
        form.submit((HtmlButton) last(form.getHtmlElementsByTagName("button")))

        JiraBuilderTrigger jiraBuilderTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraBuilderTrigger)
        assertThat(jiraBuilderTrigger.commentPattern, is(commentPattern))
    }
}
