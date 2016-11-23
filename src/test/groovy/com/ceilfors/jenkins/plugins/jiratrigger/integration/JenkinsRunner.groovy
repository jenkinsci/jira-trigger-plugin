package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerExecutor
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhook
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.*
import jenkins.model.GlobalConfiguration
import org.jvnet.hudson.test.JenkinsRule

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
class JenkinsRunner extends JenkinsRule {

    private JenkinsBlockingQueue jenkinsQueue

    @Override
    void before() throws Throwable {
        super.before()
        jenkins.quietPeriod = 100
        jenkinsQueue = new JenkinsBlockingQueue(instance)

        JulLogLevelRule.configureLog() // Needed when @IgnoreRest is used in acceptance tests
    }

    void buildShouldBeScheduled(String jobName) {
        Queue.Item scheduledItem = jenkinsQueue.getScheduledItem()
        assertThat("Build is not scheduled!", scheduledItem, is(not(nullValue())))
        assertThat("Last build scheduled doesn't match the job name asserted", (scheduledItem.task as Project).fullName, is(jobName))
    }

    void noBuildShouldBeScheduled() {
        if (jenkinsQueue.isItemScheduled()) {
            Queue.Item scheduledItem = jenkinsQueue.scheduledItem
            assertThat("Build is scheduled: ${(scheduledItem.task as Project).fullName}", scheduledItem, is(nullValue()))
        }
    }

    void buildShouldBeScheduledWithParameter(String jobName, Map<String, String> parameterMap) {
        Queue.Item scheduledItem = jenkinsQueue.scheduledItem
        assertThat("Build is not scheduled, check logs!", scheduledItem, is(not(nullValue())))
        assertThat("Last build scheduled doesn't match the job name asserted", (scheduledItem.task as Project).fullName, is(jobName))
        def parametersAction = scheduledItem.getAction(ParametersAction)
        assertThat(parametersAction.parameters, containsInAnyOrder(*parameterMap.collect { key, value -> new StringParameterValue(key, value) }))
    }

    public JiraTriggerExecutor getJiraTriggerExecutor() {
        instance.getInjector().getInstance(JiraTriggerExecutor)
    }

    public JiraWebhook getJiraWebhook() {
        instance.getActions().find { it instanceof JiraWebhook } as JiraWebhook
    }

    String getWebhookUrl() {
        return "${getURL().toString()}${jiraWebhook.urlName}/"
    }

    JiraChangelogTriggerConfigurer createJiraChangelogTriggeredProject(String name) {
        FreeStyleProject project = createFreeStyleProject(name)

        JiraChangelogTriggerConfigurer jenkinsChangelogRunner = new JiraChangelogTriggerConfigurer(this, name)
        JiraTriggerConfigurationPage configPage = jenkinsChangelogRunner.configure()
        configPage.activate()
        configPage.save()

        assertThat(project.triggers.values(), hasItem(instanceOf(JiraChangelogTrigger)))
        return jenkinsChangelogRunner
    }

    JiraCommentTriggerConfigurer createJiraCommentTriggeredProject(String name, String... parameters) {
        FreeStyleProject project = createFreeStyleProject(name)
        project.addProperty(new ParametersDefinitionProperty(parameters.collect {
            new StringParameterDefinition(it, "")
        }))

        JiraCommentTriggerConfigurer jenkinsCommentRunner = new JiraCommentTriggerConfigurer(this, name)
        JiraTriggerConfigurationPage configPage = jenkinsCommentRunner.configure()
        configPage.activate()
        configPage.save()

        assertThat(project.triggers.values(), hasItem(instanceOf(JiraCommentTrigger)))
        return jenkinsCommentRunner
    }

    JiraTriggerGlobalConfigurationPage globalConfigure() {
        HtmlPage htmlPage = createWebClient().goTo("configure")
        return new JiraTriggerGlobalConfigurationPage(htmlPage)
    }

    void setJiraTriggerGlobalConfig(String rootUrl, String username, String password) {
        JiraTriggerGlobalConfigurationPage configPage = globalConfigure()
        configPage.setRootUrl(rootUrl)
        configPage.setCredentials(username, password)
        configPage.save()

        def globalConfig = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        assertThat(globalConfig.jiraRootUrl, equalTo(rootUrl))
        assertThat(globalConfig.jiraUsername, equalTo(username))
        assertThat(globalConfig.jiraPassword.plainText, equalTo(password))
    }

    def setJiraCommentReply(boolean active) {
        JiraTriggerGlobalConfigurationPage configPage = globalConfigure()
        configPage.setJiraCommentReply(active)
        configPage.save()

        def globalConfig = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        assertThat(globalConfig.jiraCommentReply, equalTo(active))
    }
}
