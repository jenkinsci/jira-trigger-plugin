package com.ceilfors.jenkins.plugins.jirabuilder

import com.atlassian.jira.rest.client.api.domain.Comment
import hudson.model.BuildableItem
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author ceilfors
 */
@SuppressWarnings("GroovyAssignabilityCheck")
class JiraCommentBuilderTriggerTest extends Specification {

    def project

    def setup() {
        given:
        project = Mock(BuildableItem)
        project.getFullName() >> "project"
    }

    @Unroll
    def "Triggers build when comment body matches the comment pattern"(String commentBody, String commentPattern) {
        given:
        def comment = new Comment(null, commentBody, null, null, null, null, null, null)
        JiraCommentBuilderTrigger trigger = new JiraCommentBuilderTrigger(commentPattern: commentPattern)

        when:
        trigger.start(project, false)
        trigger.run(comment)

        then:
        1 * project.scheduleBuild(_, _)

        where:
        commentBody                       | commentPattern
        "please build me"                 | "please build me"
        "please build me"                 | "(?i)please build me"
        "PLEASE BUILD ME"                 | "(?i)please build me"
        "start\n\nplease build me\n\nend" | "(?s).*please build me.*"
    }

    @Unroll
    def "Does not trigger build when comment body matches the comment pattern"(String commentBody, String commentPattern) {
        given:
        def comment = new Comment(null, commentBody, null, null, null, null, null, null)
        JiraCommentBuilderTrigger trigger = new JiraCommentBuilderTrigger(commentPattern: commentPattern)

        when:
        trigger.start(project, false)
        trigger.run(comment)

        then:
        0 * project.scheduleBuild(_, _)

        where:
        commentBody              | commentPattern
        "please do not build me" | "please build me"
        " please build me"       | "please build me"
        "please build me\n"      | "please build me"
    }
}
