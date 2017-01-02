package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.ui.JiraChangelogTriggerConfigurer
import com.ceilfors.jenkins.plugins.jiratrigger.ui.JiraCommentTriggerConfigurer
import com.ceilfors.jenkins.plugins.jiratrigger.ui.JiraTriggerConfigurer
import hudson.model.FreeStyleProject
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasItem
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat
/**
 * @author ceilfors
 */
class UiTest extends Specification {

    @Rule
    JenkinsRule jenkins = new JenkinsRule()

    def JiraTriggerConfigurer createConfigurer(Class triggerType, JenkinsRule jenkinsRule, String jobName) {
        if (triggerType == JiraCommentTrigger) {
            new JiraCommentTriggerConfigurer(jenkins, 'job')
        } else if (triggerType == JiraChangelogTrigger) {
            new JiraChangelogTriggerConfigurer(jenkins, 'job')
        } else {
            throw new UnsupportedOperationException("Trigger $triggerType is unsupported")
        }
    }

    def 'Sets JQL filter'() {
        given:
        def jqlFilter = 'non default jql filter'
        FreeStyleProject project = jenkins.createFreeStyleProject('job')
        JiraTriggerConfigurer configurer = createConfigurer(triggerType, jenkins, 'job')

        when:
        configurer.activate()

        then:
        assertThat(project.triggers.values(), hasItem(instanceOf(triggerType)))

        when:
        configurer.setJqlFilter(jqlFilter)
        def trigger = project.getTrigger(triggerType)

        then:
        assertThat(trigger.jqlFilter, is(jqlFilter))

        where:
        triggerType << [JiraCommentTrigger, JiraChangelogTrigger]
    }


    def 'Adds parameter mappings'() {
        given:
        FreeStyleProject project = jenkins.createFreeStyleProject('job')
        JiraTriggerConfigurer configurer = createConfigurer(triggerType, jenkins, 'job')

        when:
        configurer.activate()
        def originalParameterMappingSize = project.getTrigger(triggerType).parameterMappings.size()
        configurer.addParameterMapping('parameter1', 'path1')
        def trigger = project.getTrigger(triggerType)

        then:
        assertThat("Parameter mapping is not added", trigger.parameterMappings.size(), equalTo(originalParameterMappingSize + 1))
        assertThat(trigger.parameterMappings.last().jenkinsParameter, is('parameter1'))
        assertThat(trigger.parameterMappings.last().issueAttributePath, is('path1'))

        when:
        originalParameterMappingSize = project.getTrigger(triggerType).parameterMappings.size()
        configurer.addParameterMapping('parameter2', 'path2')
        trigger = project.getTrigger(triggerType)

        then:
        assertThat("Parameter mapping is not added", trigger.parameterMappings.size(), equalTo(originalParameterMappingSize + 1))
        assertThat(trigger.parameterMappings.last().jenkinsParameter, is('parameter2'))
        assertThat(trigger.parameterMappings.last().issueAttributePath, is('path2'))

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
        assertThat(trigger.commentPattern, is(commentPattern))
    }

    def 'Adds field matchers'() {
        given:
        FreeStyleProject project = jenkins.createFreeStyleProject('job')
        def configurer = new JiraChangelogTriggerConfigurer(jenkins, 'job')

        when:
        configurer.activate()
        configurer.addCustomFieldChangelogMatcher('Custom Field 1', 'old 1', 'new 1')
        configurer.addJiraFieldChangelogMatcher('Jira Field 1', 'old 2', 'new 2')
        configurer.addCustomFieldChangelogMatcher('Custom Field 2', 'old 3', 'new 3')
        configurer.addJiraFieldChangelogMatcher('Jira Field 2', 'old 4', 'new 4')
        def trigger = project.getTrigger(JiraChangelogTrigger)
        def matchers = trigger.changelogMatchers

        then:
        assertThat("Changelog matchers were not added correctly", matchers.size(), equalTo(4))
        assertThat(matchers[0].field, is('Custom Field 1'))
        assertThat(matchers[0].oldValue, is('old 1'))
        assertThat(matchers[0].newValue, is('new 1'))
        assertThat(matchers[1].field, is('Jira Field 1'))
        assertThat(matchers[1].oldValue, is('old 2'))
        assertThat(matchers[1].newValue, is('new 2'))
        assertThat(matchers[2].field, is('Custom Field 2'))
        assertThat(matchers[2].oldValue, is('old 3'))
        assertThat(matchers[2].newValue, is('new 3'))
        assertThat(matchers[3].field, is('Jira Field 2'))
        assertThat(matchers[3].oldValue, is('old 4'))
        assertThat(matchers[3].newValue, is('new 4'))
    }
}
