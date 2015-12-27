package com.ceilfors.jenkins.plugins.jirabuilder.webhook
import com.ceilfors.jenkins.plugins.jirabuilder.jira.Jira
import net.sf.json.JSONObject
import spock.lang.Specification

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
        Map commentResult = [:]

        given:
        def listener = Mock(JiraWebHookListener)
        JiraWebHook jiraWebHook = new JiraWebHook(jira: Mock(Jira))
        jiraWebHook.setJiraWebHookListener(listener)

        when:
        jiraWebHook.processEvent(createCommentCreatedEvent(), "TEST-123")

        then:
        1 * listener.commentCreated(_, _) >> { issue, comment -> commentResult = comment }
        expect commentResult.body, is("comment body")
        expect commentResult.author.name, is("admin")
    }

    def "Should hit JIRA to get issue details"() {
        Map issueResult = [:]

        given:
        def issueKey = "TEST-123"
        def listener = Mock(JiraWebHookListener)
        Jira jira = Mock(Jira)
        JiraWebHook jiraWebHook = new JiraWebHook(jira: jira)
        jiraWebHook.setJiraWebHookListener(listener)

        when:
        jiraWebHook.processEvent(createCommentCreatedEvent(), issueKey)

        then:
        1 * jira.getIssueMap(issueKey) >> [key: issueKey]
        1 * listener.commentCreated(_, _) >> { issue, comment -> println issue;issueResult = issue }
        expect issueResult.key, is(issueKey)
    }

    def "Should not notify listener when the event type is not comment_created"() {
        given:
        JiraWebHook jiraWebHook = new JiraWebHook(jira: Mock(Jira))

        def listener = Mock(JiraWebHookListener)
        jiraWebHook.setJiraWebHookListener(listener)

        when:
        jiraWebHook.processEvent(createIssueCreatedEvent(), "TEST-123")

        then:
        0 * listener.commentCreated(_, _)
    }
}
