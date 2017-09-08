package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentReplier
import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerExecutor
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerGlobalConfiguration
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.webhook.JiraWebhook
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.FreeStyleProject
import hudson.model.ParametersAction
import hudson.model.Queue
import hudson.model.StringParameterValue
import jenkins.model.GlobalConfiguration
import org.jvnet.hudson.test.JenkinsRule

import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.empty
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.not
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
class JenkinsRunner extends JenkinsRule {

    private JenkinsBlockingQueue jenkinsQueue

    @Override
    void before() throws Throwable {
        super.before()
        jenkins.quietPeriod = 0
        jenkinsQueue = new JenkinsBlockingQueue(instance)

        JulLogLevelRule.configureLog() // Needed when @IgnoreRest is used in acceptance tests
    }

    AbstractBuild getScheduledBuild(AbstractProject project) {
        Queue.Item item = project.queueItem
        if (item == null) {
            project.getBuildByNumber(1)
        } else {
            item.future.get()
            (item.future.startCondition.get() as AbstractBuild)
        }
    }

    void buildShouldBeScheduled(String jobName, Map<String, String> parameterMap = [:]) {
        Collection<AbstractProject> jobs  = jenkinsQueue.scheduledJobs
        assertThat('Build is not scheduled!', jobs, is(not(empty())))
        assertThat('Only one project is scheduled', jobs.size(), is(equalTo(1)))

        AbstractProject job = jobs.first()
        assertThat('Last build scheduled does not match the job name asserted', job.fullName, is(jobName))
        if (parameterMap) {
            AbstractBuild build = getScheduledBuild(job)
            def parametersAction = build.getAction(ParametersAction)
            assertThat(parametersAction.parameters,
                    containsInAnyOrder(*parameterMap.collect { key, value -> new StringParameterValue(key, value) }))
        }
    }

    void noBuildShouldBeScheduled() {
        if (jenkinsQueue.isAnyJobScheduled()) {
            Collection<AbstractProject> jobs  = jenkinsQueue.scheduledJobs
            assertThat("Build is scheduled: ${jobs*.fullName}", jobs, is(empty()))
        }
    }

    JiraTriggerExecutor getJiraTriggerExecutor() {
        instance.injector.getInstance(JiraTriggerExecutor)
    }

    JiraWebhook getJiraWebhook() {
        instance.actions.find { it instanceof JiraWebhook } as JiraWebhook
    }

    String getWebhookUrl() {
        "${URL.toString()}${jiraWebhook.urlName}/"
    }

    JiraChangelogTriggerProject createJiraChangelogTriggeredProject(String name) {
        FreeStyleProject project = createFreeStyleProject(name)
        def trigger = new JiraChangelogTrigger()
        project.addTrigger(trigger)
        project.save()
        trigger.start(project, true)
        new JiraChangelogTriggerProject(project)
    }

    JiraCommentTriggerProject createJiraCommentTriggeredProject(String name) {
        FreeStyleProject project = createFreeStyleProject(name)
        def trigger = new JiraCommentTrigger()
        project.addTrigger(trigger)
        project.save()
        trigger.start(project, true)
        new JiraCommentTriggerProject(project)
    }

    def setJiraCommentReply(boolean active) {
        def globalConfig = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        globalConfig.jiraCommentReply = active
        globalConfig.save()
    }

    void setJiraClient(JiraClient jiraClient) {
        // KLUDGE: Could not find a better way to override Guice injection
        jenkins.getDescriptorByType(JiraChangelogTrigger.JiraChangelogTriggerDescriptor).jiraClient = jiraClient
        jenkins.getDescriptorByType(JiraCommentTrigger.JiraCommentTriggerDescriptor).jiraClient = jiraClient
        jenkins.injector.getInstance(JiraTriggerExecutor).jiraTriggerListeners
                .grep(JiraCommentReplier)[0].jiraClient = jiraClient
    }

    void setQuietPeriod(int quietPeriod) {
        this.jenkins.quietPeriod = quietPeriod
    }
}
