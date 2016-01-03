package com.ceilfors.jenkins.plugins.jirabuilder.webhook

import org.kohsuke.stapler.StaplerRequest
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static spock.util.matcher.HamcrestSupport.expect
/**
 * @author ceilfors
 */
class JiraWebhookTest extends Specification {

    String createCommentCreatedEvent() {
        this.class.getResourceAsStream("/webhook-request-sample/comment_created.json").text
    }

    String createIssueCreatedEvent() {
        this.class.getResourceAsStream("/webhook-request-sample/issue_created.json").text
    }

    def "Should notify listener when a comment event is received"() {
        WebhookCommentEvent commentEvent = null

        given:
        def listener = Mock(JiraWebhookListener)
        JiraWebhook jiraWebhook = new JiraWebhook()
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createCommentCreatedEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> commentEvent = args[0] }
        expect commentEvent.comment.body, is("comment body")
        expect commentEvent.comment.author.name, is("admin")
    }

    def "Should store request parameter in context"() {
        WebhookCommentEvent commentEvent = null

        given:
        def listener = Mock(JiraWebhookListener)
        def staplerRequest = Mock(StaplerRequest)
        staplerRequest.getParameter("user_id") >> "adminId"
        staplerRequest.getParameter("user_key") >> "adminKey"
        JiraWebhook jiraWebhook = new JiraWebhook()
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(staplerRequest, createCommentCreatedEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> commentEvent = args[0] }
        expect commentEvent.userId, equalTo("adminId")
        expect commentEvent.userKey, equalTo("adminKey")
    }

    def "Should not notify listener when the event type is not comment_created"() {
        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueCreatedEvent())

        then:
        0 * listener.commentCreated(_)
    }
}
