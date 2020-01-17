package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.changelog.CustomFieldChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.JiraFieldChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import com.ceilfors.jenkins.plugins.jiratrigger.ui.JiraChangelogTriggerConfigurer
import com.ceilfors.jenkins.plugins.jiratrigger.ui.JiraCommentTriggerConfigurer
import com.ceilfors.jenkins.plugins.jiratrigger.ui.JiraTriggerConfigurer
import com.ceilfors.jenkins.plugins.jiratrigger.ui.JiraTriggerGlobalConfigurationPage
import hudson.model.FreeStyleProject
import jenkins.model.GlobalConfiguration
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

import static org.hamcrest.Matchers.hasItem
import static org.hamcrest.Matchers.instanceOf
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
class UiTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    JiraTriggerConfigurer createUiConfigurer(Class triggerType, String jobName) {
        if (triggerType == JiraCommentTrigger) {
            new JiraCommentTriggerConfigurer(jenkins, jobName)
        } else if (triggerType == JiraChangelogTrigger) {
            new JiraChangelogTriggerConfigurer(jenkins, jobName)
        } else {
            throw new UnsupportedOperationException("Trigger $triggerType is unsupported")
        }
    }

    def 'Sets Global configuration'() {
        given:
        def configPage = new JiraTriggerGlobalConfigurationPage(jenkins.createWebClient().goTo('configure'))

        when:
        configPage.setRootUrl('test root')
        configPage.setCredentials('test user', 'test password')
        configPage.setJiraCommentReply(true)
        configPage.save()

        then:
        def globalConfig = GlobalConfiguration.all().get(JiraTriggerGlobalConfiguration)
        globalConfig.jiraCommentReply
        globalConfig.jiraRootUrl == 'test root'
        globalConfig.jiraUsername == 'test user'
        globalConfig.jiraPassword.plainText == 'test password'
    }

    def 'Adds parameter mappings'() {
        given:
        FreeStyleProject project = jenkins.createFreeStyleProject('job')
        JiraTriggerConfigurer configurer = createUiConfigurer(triggerType, 'job')

        when:
        configurer.activate()
        configurer.addParameterMapping('parameter1', 'path1')
        configurer.addParameterMapping('parameter2', 'path2')
        def trigger = project.getTrigger(triggerType)

        then:
        trigger.parameterMappings.size() == 2
        trigger.parameterMappings[0] == new IssueAttributePathParameterMapping('parameter1', 'path1')
        trigger.parameterMappings[1] == new IssueAttributePathParameterMapping('parameter2', 'path2')

        where:
        triggerType << [JiraCommentTrigger, JiraChangelogTrigger]
    }

    def 'Sets comment pattern'() {
        given:
        def commentPattern = 'non default comment pattern'
        FreeStyleProject project = jenkins.createFreeStyleProject('job')
        def configurer = new JiraCommentTriggerConfigurer(jenkins, 'job')

        when:
        configurer.activate()
        configurer.setCommentPattern(commentPattern)
        def trigger = project.getTrigger(JiraCommentTrigger)

        then:
        trigger.commentPattern == commentPattern
    }

    def 'Adds field matchers'() {
        given:
        FreeStyleProject project = jenkins.createFreeStyleProject('job')
        def configurer = new JiraChangelogTriggerConfigurer(jenkins, 'job')

        when:
        configurer.activate()
        configurer.addCustomFieldChangelogMatcher('Custom Field 1', 'old 1', '')
        configurer.addJiraFieldChangelogMatcher('Jira Field 1', '', 'new 2')
        configurer.addCustomFieldChangelogMatcher('Custom Field 2', '', 'new 3')
        configurer.addJiraFieldChangelogMatcher('Jira Field 2', 'old 4', '')
        def matchers = project.getTrigger(JiraChangelogTrigger).changelogMatchers

        def matcher0 = new CustomFieldChangelogMatcher('Custom Field 1', '', 'old 1')
        matcher0.comparingNewValue = false
        def matcher1 = new JiraFieldChangelogMatcher('Jira Field 1', 'new 2', '')
        matcher1.comparingOldValue = false
        def matcher2 = new CustomFieldChangelogMatcher('Custom Field 2', 'new 3', '')
        matcher2.comparingOldValue = false
        def matcher3 = new JiraFieldChangelogMatcher('Jira Field 2', '', 'old 4')
        matcher3.comparingNewValue = false

        then:
        matchers.size() == 4
        matchers[0] == matcher0
        matchers[1] == matcher1
        matchers[2] == matcher2
        matchers[3] == matcher3
    }
}
