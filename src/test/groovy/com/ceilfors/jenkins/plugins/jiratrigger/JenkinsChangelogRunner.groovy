package com.ceilfors.jenkins.plugins.jiratrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.AbstractProject
import jenkins.model.Jenkins

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat

/**
 * @author ceilfors
 */
class JenkinsChangelogRunner {

    private JenkinsRunner jenkinsRunner
    private Jenkins instance
    private String jobName

    public JenkinsChangelogRunner(JenkinsRunner jenkinsRunner, String jobName) {
        this.jenkinsRunner = jenkinsRunner
        this.instance = jenkinsRunner.instance
        this.jobName = jobName
    }

    void setJqlFilter(String jqlFilter) {
        JiraTriggerConfigurationPage configPage = configure()
        configPage.setJqlFilter(jqlFilter)
        configPage.save()

        JiraChangelogTrigger jiraChangelogTrigger = getTrigger()
        assertThat(jiraChangelogTrigger.jqlFilter, is(jqlFilter))
    }

    JiraChangelogTriggerConfigurationPage configure() {
        HtmlPage htmlPage = jenkinsRunner.createWebClient().goTo("job/$jobName/configure")
        return new JiraChangelogTriggerConfigurationPage(htmlPage)
    }

    private JiraChangelogTrigger getTrigger() {
        instance.getItemByFullName(jobName, AbstractProject).getTrigger(JiraChangelogTrigger)
    }

    void addChangelogMatcher(String fieldId, String toValue) {

    }
}
