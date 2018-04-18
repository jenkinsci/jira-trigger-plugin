package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Comment
import hudson.model.AbstractProject
import hudson.model.Item
import hudson.model.ItemGroup
import hudson.model.Queue
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification
import spock.lang.Unroll

import static com.ceilfors.jenkins.plugins.jiratrigger.TestUtils.createIssue

/**
 * @author ceilfors
 */
@SuppressWarnings('GroovyAssignabilityCheck')
class JiraCommentTriggerTest extends Specification {

    def project

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    private static interface TaskItem extends Queue.Task, Item { }

    def setup() {
        def projectParent = Mock(ItemGroup)
        projectParent.getFullName() >> ''
        project = Mock(AbstractProject)
        project.getOwnerTask() >> Mock(TaskItem)
        project.getParent() >> projectParent
        project.getName() >> 'project'
        project.isBuildable() >> true
    }

    @Unroll
    def 'Triggers build when comment body matches the comment pattern'(String commentBody, String commentPattern) {
        given:
        def comment = new Comment(null, commentBody, null, null, null, null, null, null)
        JiraCommentTrigger trigger = new JiraCommentTrigger(commentPattern: commentPattern)
        trigger.job = project

        when:
        boolean result = trigger.run(createIssue('TEST-123'), comment)

        then:
        result

        where:
        commentBody                       | commentPattern
        'please build me'                 | 'please build me'
        'please build me'                 | '(?i)please build me'
        'PLEASE BUILD ME'                 | '(?i)please build me'
        'start\n\nplease build me\n\nend' | '(?s).*please build me.*'
    }

    @Unroll
    def 'Does not trigger build when comment body matches the comment pattern'(String commentBody,
                                                                               String commentPattern) {
        given:
        def comment = new Comment(null, commentBody, null, null, null, null, null, null)
        JiraCommentTrigger trigger = new JiraCommentTrigger(commentPattern: commentPattern)
        trigger.job = project

        when:
        boolean result = trigger.run(createIssue('TEST-123'), comment)

        then:
        !result

        where:
        commentBody              | commentPattern
        'please do not build me' | 'please build me'
        ' please build me'       | 'please build me'
        'please build me\n'      | 'please build me'
    }
}
