package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.ChangelogItem
import com.atlassian.jira.rest.client.api.domain.FieldType
import org.kohsuke.stapler.StaplerRequest
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static spock.util.matcher.HamcrestSupport.expect
/**
 * @author ceilfors
 */
@SuppressWarnings("GroovyAssignabilityCheck")
class JiraWebhookTest extends Specification {

    String createCommentCreatedEvent() {
        this.class.getResourceAsStream("comment_created.json").text
    }

    String createIssueCreatedEvent() {
        this.class.getResourceAsStream("issue_created.json").text
    }

    String createIssueUpdatedEvent() {
        this.class.getResourceAsStream("issue_updated_without_comment.json").text
    }

    String createIssueUpdatedWithCommentEvent() {
        this.class.getResourceAsStream("issue_updated_with_comment.json").text
    }

    String createIssueStatusUpdatedEvent() {
        this.class.getResourceAsStream("issue_updated_status_updated.json").text
    }

    @SuppressWarnings("GrReassignedInClosureLocalVar")
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
        expect commentEvent.webhookEventType, is(JiraWebhook.WEBHOOK_EVENT)
    }

    def "Should fire changelog created event when status field is updated"() {
        WebhookChangelogEvent changelogEvent = null

        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueStatusUpdatedEvent())

        then:
        1 * listener.changelogCreated(_) >> { args -> changelogEvent = args[0] }
        expect changelogEvent.changelog.items.toList(), equalTo([
                new ChangelogItem(FieldType.JIRA, "resolution", "1", "Fixed", "10000", "Done"),
                new ChangelogItem(FieldType.JIRA, "status", "10000", "To Do", "10001", "Done")
        ])
    }

    @SuppressWarnings("GrReassignedInClosureLocalVar")
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

    def "Should not notify listener when the event type is issue created"() {
        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueCreatedEvent())

        then:
        0 * listener.commentCreated(_)
    }

    @SuppressWarnings("GrReassignedInClosureLocalVar")
    def "Should not notify listener when issue is updated with comment"() {
        WebhookCommentEvent commentEvent = null

        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueUpdatedWithCommentEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> commentEvent = args[0] }
        expect commentEvent.comment.body, is("comment body")
        expect commentEvent.comment.author.name, is("admin")
        expect commentEvent.webhookEventType, is(JiraWebhook.WEBHOOK_EVENT)
    }

    def "Should not fire comment created event when a comment is added to an issue"() {
        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueUpdatedEvent())

        then:
        0 * listener.commentCreated(_)
    }
}
