package com.ceilfors.jenkins.plugins.jiratrigger
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
        jiraTriggerExecutor.setQuietPeriod(100)
        jenkinsQueue = new JenkinsBlockingQueue(instance)
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
        assertThat("Build is not scheduled!", scheduledItem, is(not(nullValue())))
        assertThat("Last build scheduled doesn't match the job name asserted", (scheduledItem.task as Project).fullName, is(jobName))
        def parametersAction = scheduledItem.getAction(ParametersAction)
        assertThat(parametersAction.parameters, containsInAnyOrder(*parameterMap.collect { key, value -> new StringParameterValue(key, value) }))
    }

    public JiraTriggerExecutor getJiraTriggerExecutor() {
        instance.getInjector().getInstance(JiraTriggerExecutor)
    }

    private JiraWebhook getJiraWebhook() {
        instance.getActions().find { it instanceof JiraWebhook } as JiraWebhook
    }

    String getWebhookUrl() {
        return "${getURL().toString()}${jiraWebhook.urlName}/"
    }

    JenkinsChangelogRunner createJiraChangelogTriggeredProject(String name) {
        FreeStyleProject project = createFreeStyleProject(name)

        JenkinsChangelogRunner jenkinsChangelogRunner = new JenkinsChangelogRunner(this, name)
        JiraTriggerConfigurationPage configPage = jenkinsChangelogRunner.configure()
        configPage.activate()
        configPage.save()

        assertThat(project.triggers.values(), hasItem(instanceOf(JiraChangelogTrigger)))
        return jenkinsChangelogRunner
    }

    JenkinsCommentRunner createJiraCommentTriggeredProject(String name, String... parameters) {
        FreeStyleProject project = createFreeStyleProject(name)
        project.addProperty(new ParametersDefinitionProperty(parameters.collect {
            new StringParameterDefinition(it, "")
        }))

        JenkinsCommentRunner jenkinsCommentRunner = new JenkinsCommentRunner(this, name)
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
