package com.ceilfors.jenkins.plugins.jiratrigger.ui

import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.jvnet.hudson.test.JenkinsRule
/**
 * @author ceilfors
 */
class JiraIssueCreatedTriggerConfigurer extends JiraTriggerConfigurer {

    JiraIssueCreatedTriggerConfigurer(JenkinsRule jenkinsRule, String jobName) {
        super(jenkinsRule, jobName)
    }

    JiraIssueCreatedTriggerConfigurationPage configure() {
        HtmlPage htmlPage = jenkinsRule.createWebClient().goTo("job/$jobName/configure")
        new JiraIssueCreatedTriggerConfigurationPage(htmlPage)
    }
}
