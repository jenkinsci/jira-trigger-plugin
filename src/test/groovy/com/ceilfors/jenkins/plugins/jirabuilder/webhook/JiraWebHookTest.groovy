package com.ceilfors.jenkins.plugins.jirabuilder.webhook

import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static spock.util.matcher.HamcrestSupport.expect
/**
 * @author ceilfors
 */
class JiraWebhookTest extends Specification {

    Map createCommentCreatedEvent() {
        JSONObject o = JSONObject.fromObject(this.class.getResourceAsStream("/webhook-request-sample/comment_created.json").text)
        return JSONObject.toBean(o, Map) as Map
    }

    Map createIssueCreatedEvent() {
        JSONObject o = JSONObject.fromObject(this.class.getResourceAsStream("/webhook-request-sample/issue_created.json").text)
        return JSONObject.toBean(o, Map) as Map
    }

    def "Should notify listener when a comment event is received"() {
        JiraWebhookContext context = null

        given:
        def listener = Mock(JiraWebhookListener)
        JiraWebhook jiraWebhook = new JiraWebhook()
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), "TEST-123", createCommentCreatedEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> context = args[0] }
        expect context.eventBody.body, is("comment body")
        expect context.eventBody.author.name, is("admin")
    }

    def "Should get store request parameter in context"() {
        JiraWebhookContext context = null

        given:
        def listener = Mock(JiraWebhookListener)
        def staplerRequest = Mock(StaplerRequest)
        staplerRequest.getParameter("user_id") >> "adminId"
        staplerRequest.getParameter("user_key") >> "adminKey"
        JiraWebhook jiraWebhook = new JiraWebhook()
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(staplerRequest, "TEST-123", createCommentCreatedEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> context = args[0] }
        expect context.userId, equalTo("adminId")
        expect context.userKey, equalTo("adminKey")
    }

    def "Should not notify listener when the event type is not comment_created"() {
        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), "TEST-123", createIssueCreatedEvent())

        then:
        0 * listener.commentCreated(_)
    }
}
