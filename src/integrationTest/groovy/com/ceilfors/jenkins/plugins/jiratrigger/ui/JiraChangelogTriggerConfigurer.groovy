package com.ceilfors.jenkins.plugins.jiratrigger.ui

import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.jvnet.hudson.test.JenkinsRule
/**
 * @author ceilfors
 */
class JiraChangelogTriggerConfigurer extends JiraTriggerConfigurer {

    JiraChangelogTriggerConfigurer(JenkinsRule jenkinsRule, String jobName) {
        super(jenkinsRule, jobName)
    }

    JiraChangelogTriggerConfigurationPage configure() {
        JenkinsRule.WebClient webClient = jenkinsRule.createWebClient()
        webClient.options.setThrowExceptionOnScriptError(false)
        HtmlPage htmlPage = webClient.goTo("job/$jobName/configure")
        new JiraChangelogTriggerConfigurationPage(htmlPage)
    }

    void addJiraFieldChangelogMatcher(String fieldId, String oldValue, String newValue) {
        JiraChangelogTriggerConfigurationPage configPage = configure()
        configPage.addJiraFieldChangelogMatcher(fieldId, oldValue, newValue)
        configPage.save()
    }

    void addCustomFieldChangelogMatcher(String fieldName, String oldValue, String newValue) {
        JiraChangelogTriggerConfigurationPage configPage = configure()
        configPage.addCustomFieldChangelogMatcher(fieldName, oldValue, newValue)
        configPage.save()
    }
}
