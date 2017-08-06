package com.ceilfors.jenkins.plugins.jiratrigger

import hudson.model.FreeStyleProject
import org.junit.Rule
import org.jvnet.hudson.test.Issue
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Ignore
import spock.lang.Specification

import static com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger.JiraCommentTriggerDescriptor

/**
 * @author ceilfors
 */
class JiraTriggerTest extends Specification {

    def project

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    def 'Should add/remove trigger when a trigger is created/deleted'() {
        setup:
        JiraCommentTriggerDescriptor descriptor = jenkinsRule.instance.getDescriptor(JiraCommentTrigger)

        when:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('job')
        def trigger = new JiraCommentTrigger()
        trigger.start(job, true)

        then:
        descriptor.allTriggers().size() == 1

        when:
        trigger.stop()

        then:
        descriptor.allTriggers().size() == 0
    }

    def 'Should be able to delete trigger after a job is renamed'() {
        setup:
        JiraCommentTriggerDescriptor descriptor = jenkinsRule.instance.getDescriptor(JiraCommentTrigger)

        when:
        FreeStyleProject job = jenkinsRule.createFreeStyleProject('job')
        def trigger = new JiraCommentTrigger()
        trigger.start(job, true)
        job.renameTo('newjob')
        trigger.stop()

        then:
        descriptor.allTriggers().size() == 0
    }

    @Issue('JENKINS-43642')
    @Ignore
    def 'Should be able to stop JiraTrigger when the trigger is not started yet'() {
        setup:
        JiraCommentTriggerDescriptor descriptor = jenkinsRule.instance.getDescriptor(JiraCommentTrigger)

        when:
        def trigger = new JiraCommentTrigger()
        trigger.stop()

        then:
        descriptor.allTriggers().size() == 0
    }
}
