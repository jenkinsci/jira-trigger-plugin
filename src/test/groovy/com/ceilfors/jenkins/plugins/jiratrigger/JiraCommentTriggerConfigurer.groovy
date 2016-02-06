package com.ceilfors.jenkins.plugins.jiratrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.AbstractProject
import jenkins.model.Jenkins

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.isEmptyOrNullString
import static org.hamcrest.Matchers.not
import static org.junit.Assert.assertThat

/**
 * @author ceilfors
 */
class JiraCommentTriggerConfigurer {

    private JenkinsRunner jenkinsRunner
    private Jenkins instance
    private String jobName

    public JiraCommentTriggerConfigurer(JenkinsRunner jenkinsRunner, String jobName) {
        this.jenkinsRunner = jenkinsRunner
        this.instance = jenkinsRunner.instance
        this.jobName = jobName
    }

    def setCommentPattern(String commentPattern) {
        JiraTriggerConfigurationPage configPage = configure()
        configPage.setCommentPattern(commentPattern)
        configPage.save()

        JiraCommentTrigger jiraCommentTrigger = getTrigger()
        assertThat(jiraCommentTrigger.commentPattern, is(commentPattern))
    }

    void setJqlFilter(String jqlFilter) {
        JiraTriggerConfigurationPage configPage = configure()
        configPage.setJqlFilter(jqlFilter)
        configPage.save()

        JiraCommentTrigger jiraCommentTrigger = getTrigger()
        assertThat(jiraCommentTrigger.jqlFilter, is(jqlFilter))
    }


    void addParameterMapping(String jenkinsParameter, String issueAttributePath) {
        JiraCommentTrigger jiraCommentTrigger = getTrigger()
        def originalParameterMappingSize = jiraCommentTrigger.parameterMappings.size()

        JiraTriggerConfigurationPage configPage = configure()
        configPage.addParameterMapping(jenkinsParameter, issueAttributePath)
        configPage.save()

        jiraCommentTrigger = getTrigger()
        assertThat("Parameter mapping is not added", jiraCommentTrigger.parameterMappings.size(), equalTo(originalParameterMappingSize + 1))
        assertThat(jiraCommentTrigger.parameterMappings.last().jenkinsParameter, is(jenkinsParameter))
        assertThat(jiraCommentTrigger.parameterMappings.last().issueAttributePath, is(issueAttributePath))
    }

    void commentPatternShouldNotBeEmpty() {
        JiraTriggerConfigurationPage configPage = configure()
        assertThat(configPage.commentPattern, not(isEmptyOrNullString()))
    }

    JiraCommentTriggerConfigurationPage configure() {
        HtmlPage htmlPage = jenkinsRunner.createWebClient().goTo("job/$jobName/configure")
        return new JiraCommentTriggerConfigurationPage(htmlPage)
    }

    private JiraCommentTrigger getTrigger() {
        instance.getItemByFullName(jobName, AbstractProject).getTrigger(JiraCommentTrigger)
    }
}
