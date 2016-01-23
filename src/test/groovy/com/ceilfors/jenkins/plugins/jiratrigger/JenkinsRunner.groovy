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

    FreeStyleProject createJiraChangelogTriggeredProject(String name) {
        FreeStyleProject project = createFreeStyleProject(name)

        JiraTriggerConfigurationPage configPage = configure(name)
        configPage.activateJiraChangelogTrigger()
        configPage.save()

        assertThat(project.triggers.values(), hasItem(instanceOf(JiraChangelogTrigger)))
        return project
    }

    FreeStyleProject createJiraCommentTriggeredProject(String name, String... parameters) {
        FreeStyleProject project = createFreeStyleProject(name)
        project.addProperty(new ParametersDefinitionProperty(parameters.collect {
            new StringParameterDefinition(it, "")
        }))

        JiraTriggerConfigurationPage configPage = configure(name)
        configPage.activateJiraCommentTrigger()
        configPage.save()

        assertThat(project.triggers.values(), hasItem(instanceOf(JiraCommentTrigger)))
        return project
    }

    def setJiraTriggerCommentPattern(String name, String commentPattern) {
        JiraTriggerConfigurationPage configPage = configure(name)
        configPage.setCommentPattern(commentPattern)
        configPage.save()

        JiraCommentTrigger jiraCommentTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraCommentTrigger)
        assertThat(jiraCommentTrigger.commentPattern, is(commentPattern))
    }


    void setJiraTriggerJqlFilter(String name, String jqlFilter) {
        JiraTriggerConfigurationPage configPage = configure(name)
        configPage.setJqlFilter(jqlFilter)
        configPage.save()

        JiraCommentTrigger jiraCommentTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraCommentTrigger)
        assertThat(jiraCommentTrigger.jqlFilter, is(jqlFilter))
    }

    JiraTriggerConfigurationPage configure(String jobName) {
        HtmlPage htmlPage = createWebClient().goTo("job/$jobName/configure")
        return new JiraTriggerConfigurationPage(htmlPage)
    }

    JiraTriggerGlobalConfigurationPage globalConfigure() {
        HtmlPage htmlPage = createWebClient().goTo("configure")
        return new JiraTriggerGlobalConfigurationPage(htmlPage)
    }

    void addParameterMapping(String name, String jenkinsParameter, String issueAttributePath) {
        JiraCommentTrigger jiraCommentTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraCommentTrigger)
        def originalParameterMappingSize = jiraCommentTrigger.parameterMappings.size()

        JiraTriggerConfigurationPage configPage = configure(name)
        configPage.addParameterMapping(jenkinsParameter, issueAttributePath)
        configPage.save()

        jiraCommentTrigger = instance.getItemByFullName(name, AbstractProject).getTrigger(JiraCommentTrigger)
        assertThat("Parameter mapping is not added", jiraCommentTrigger.parameterMappings.size(), equalTo(originalParameterMappingSize + 1))
        assertThat(jiraCommentTrigger.parameterMappings.last().jenkinsParameter, is(jenkinsParameter))
        assertThat(jiraCommentTrigger.parameterMappings.last().issueAttributePath, is(issueAttributePath))
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

    void triggerCommentPatternShouldNotBeEmpty(String jobName) {
        JiraTriggerConfigurationPage configPage = configure(jobName)
        assertThat(configPage.commentPattern, not(isEmptyOrNullString()))
    }

    def setJiraCommentReply(boolean active) {
        JiraTriggerGlobalConfigurationPage configPage = globalConfigure()
        configPage.setJiraCommentReply(active)
        configPage.save()

        def globalConfig = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        assertThat(globalConfig.jiraCommentReply, equalTo(active))
    }
}
