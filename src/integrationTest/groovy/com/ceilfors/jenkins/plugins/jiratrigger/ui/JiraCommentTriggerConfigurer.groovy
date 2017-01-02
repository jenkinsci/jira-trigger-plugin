package com.ceilfors.jenkins.plugins.jiratrigger.ui

import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.AbstractProject
import org.jvnet.hudson.test.JenkinsRule

import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
class JiraCommentTriggerConfigurer extends JiraTriggerConfigurer {

    public JiraCommentTriggerConfigurer(JenkinsRule jenkinsRule, String jobName) {
        super(jenkinsRule, jobName)
    }

    JiraCommentTriggerConfigurationPage configure() {
        HtmlPage htmlPage = jenkinsRule.createWebClient().goTo("job/$jobName/configure")
        return new JiraCommentTriggerConfigurationPage(htmlPage)
    }

    @Deprecated
    JiraCommentTrigger getTrigger() {
        jenkins.getItemByFullName(jobName, AbstractProject).getTrigger(JiraCommentTrigger)
    }

    def setCommentPattern(String commentPattern) {
        JiraTriggerConfigurationPage configPage = configure()
        configPage.setCommentPattern(commentPattern)
        configPage.save()

        JiraCommentTrigger jiraCommentTrigger = getTrigger()
        assertThat(jiraCommentTrigger.commentPattern, is(commentPattern))
    }
}
