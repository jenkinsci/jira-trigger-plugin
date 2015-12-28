package com.ceilfors.jenkins.plugins.jirabuilder.webhook
import com.ceilfors.jenkins.plugins.jirabuilder.jira.Jira
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static spock.util.matcher.HamcrestSupport.expect
/**
 * @author ceilfors
 */
class JiraWebHookTest extends Specification {

    Map createCommentCreatedEvent() {
        JSONObject o = JSONObject.fromObject(this.class.getResourceAsStream("/webhook-request-sample/comment_created.json").text)
        return JSONObject.toBean(o, Map) as Map
    }

    Map createIssueCreatedEvent() {
        JSONObject o = JSONObject.fromObject(this.class.getResourceAsStream("/webhook-request-sample/issue_created.json").text)
        return JSONObject.toBean(o, Map) as Map
    }

    def "Should notify listener when a comment event is received"() {
        JiraWebHookContext context = null

        given:
        def listener = Mock(JiraWebHookListener)
        JiraWebHook jiraWebHook = new JiraWebHook(jira: Mock(Jira))
        jiraWebHook.setJiraWebHookListener(listener)

        when:
        jiraWebHook.processEvent(Mock(StaplerRequest), "TEST-123", createCommentCreatedEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> context = args[0] }
        expect context.eventBody.body, is("comment body")
        expect context.eventBody.author.name, is("admin")
    }

    def "Should get store request parameter in context"() {
        JiraWebHookContext context = null

        given:
        def listener = Mock(JiraWebHookListener)
        def staplerRequest = Mock(StaplerRequest)
        staplerRequest.getParameter("user_id") >> "adminId"
        staplerRequest.getParameter("user_key") >> "adminKey"
        JiraWebHook jiraWebHook = new JiraWebHook(jira: Mock(Jira))
        jiraWebHook.setJiraWebHookListener(listener)

        when:
        jiraWebHook.processEvent(staplerRequest, "TEST-123", createCommentCreatedEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> context = args[0] }
        expect context.userId, equalTo("adminId")
        expect context.userKey, equalTo("adminKey")
    }

    def "Should not notify listener when the event type is not comment_created"() {
        given:
        JiraWebHook jiraWebHook = new JiraWebHook(jira: Mock(Jira))

        def listener = Mock(JiraWebHookListener)
        jiraWebHook.setJiraWebHookListener(listener)

        when:
        jiraWebHook.processEvent(Mock(StaplerRequest), "TEST-123", createIssueCreatedEvent())

        then:
        0 * listener.commentCreated(_)
    }
}
