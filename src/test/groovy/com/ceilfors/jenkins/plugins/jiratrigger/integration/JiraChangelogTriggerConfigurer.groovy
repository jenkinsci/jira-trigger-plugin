package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.AbstractProject
import org.jvnet.hudson.test.JenkinsRule

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

/**
 * @author ceilfors
 */
class JiraChangelogTriggerConfigurer extends JiraTriggerConfigurer {

    public JiraChangelogTriggerConfigurer(JenkinsRunner jenkinsRunner, String jobName) {
        super(jenkinsRunner, jobName)
    }

    JiraChangelogTriggerConfigurationPage configure() {
        JenkinsRule.WebClient webClient = jenkinsRunner.createWebClient()
        webClient.setThrowExceptionOnScriptError(false)
        HtmlPage htmlPage = webClient.goTo("job/$jobName/configure")
        return new JiraChangelogTriggerConfigurationPage(htmlPage)
    }

    JiraChangelogTrigger getTrigger() {
        instance.getItemByFullName(jobName, AbstractProject).getTrigger(JiraChangelogTrigger)
    }

    void addJiraFieldChangelogMatcher(String fieldId, String oldValue, String newValue) {
        JiraChangelogTrigger jiraChangelogTrigger = getTrigger()
        def originalChangelogMatcherSize = jiraChangelogTrigger.changelogMatchers.size()

        JiraChangelogTriggerConfigurationPage configPage = configure()
        configPage.addJiraFieldChangelogMatcher(fieldId, oldValue, newValue)
        configPage.save()

        jiraChangelogTrigger = getTrigger()
        assertThat("Changelog matcher is not added", jiraChangelogTrigger.changelogMatchers.size(), equalTo(originalChangelogMatcherSize + 1))
        assertThat(jiraChangelogTrigger.changelogMatchers.last().field, is(fieldId))
        assertThat(jiraChangelogTrigger.changelogMatchers.last().newValue, is(newValue))
        assertThat(jiraChangelogTrigger.changelogMatchers.last().oldValue, is(oldValue))
    }

    void addJiraFieldChangelogMatcher(String fieldId, String newValue) {
        addJiraFieldChangelogMatcher(fieldId, "", newValue)
    }

    void addCustomFieldChangelogMatcher(String fieldName, String oldValue, String newValue) {
        JiraChangelogTrigger jiraChangelogTrigger = getTrigger()
        def originalChangelogMatcherSize = jiraChangelogTrigger.changelogMatchers.size()

        JiraChangelogTriggerConfigurationPage configPage = configure()
        configPage.addCustomFieldChangelogMatcher(fieldName, oldValue, newValue)
        configPage.save()

        jiraChangelogTrigger = getTrigger()
        assertThat("Changelog matcher is not added", jiraChangelogTrigger.changelogMatchers.size(), equalTo(originalChangelogMatcherSize + 1))
        assertThat(jiraChangelogTrigger.changelogMatchers.last().field, is(fieldName))
        assertThat(jiraChangelogTrigger.changelogMatchers.last().newValue, is(newValue))
        assertThat(jiraChangelogTrigger.changelogMatchers.last().oldValue, is(oldValue))
    }

    void addCustomFieldChangelogMatcher(String fieldName, String newValue) {
        addCustomFieldChangelogMatcher(fieldName, "", newValue)
    }
}
