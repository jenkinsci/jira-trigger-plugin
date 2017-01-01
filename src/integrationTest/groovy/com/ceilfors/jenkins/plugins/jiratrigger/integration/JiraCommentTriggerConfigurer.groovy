package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.AbstractProject

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
class JiraCommentTriggerConfigurer extends JiraTriggerConfigurer {

    public JiraCommentTriggerConfigurer(JenkinsRunner jenkinsRunner, String jobName) {
        super(jenkinsRunner, jobName)
    }

    JiraCommentTriggerConfigurationPage configure() {
        HtmlPage htmlPage = jenkinsRunner.createWebClient().goTo("job/$jobName/configure")
        return new JiraCommentTriggerConfigurationPage(htmlPage)
    }

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

    void commentPatternShouldNotBeEmpty() {
        JiraTriggerConfigurationPage configPage = configure()
        assertThat(configPage.commentPattern, not(isEmptyOrNullString()))
    }
}
