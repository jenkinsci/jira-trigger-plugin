package com.ceilfors.jenkins.plugins.jiratrigger.ui

import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.jvnet.hudson.test.JenkinsRule
/**
 * @author ceilfors
 */
class JiraCommentTriggerConfigurer extends JiraTriggerConfigurer {

    JiraCommentTriggerConfigurer(JenkinsRule jenkinsRule, String jobName) {
        super(jenkinsRule, jobName)
    }

    JiraCommentTriggerConfigurationPage configure() {
        HtmlPage htmlPage = jenkinsRule.createWebClient().goTo("job/$jobName/configure")
        new JiraCommentTriggerConfigurationPage(htmlPage)
    }

    def setCommentPattern(String commentPattern) {
        JiraTriggerConfigurationPage configPage = configure()
        configPage.setCommentPattern(commentPattern)
        configPage.save()
    }
}
