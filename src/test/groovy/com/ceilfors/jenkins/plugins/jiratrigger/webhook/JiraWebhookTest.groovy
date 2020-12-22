package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.api.domain.ChangelogItem
import com.atlassian.jira.rest.client.api.domain.FieldType
import org.joda.time.DateTime
import org.kohsuke.stapler.StaplerRequest
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.joda.time.DateTimeZone.UTC
import static spock.util.matcher.HamcrestSupport.expect

/**
 * @author ceilfors
 */
@SuppressWarnings(['GroovyAssignabilityCheck', 'GrReassignedInClosureLocalVar'])
class JiraWebhookTest extends Specification {

    String createIssueCreatedEvent() {
        this.class.getResourceAsStream('issue_created.json').text
    }

    String createIssueUpdatedEvent() {
        this.class.getResourceAsStream('issue_updated_without_comment.json').text
    }

    String createIssueUpdatedWithCommentEvent() {
        this.class.getResourceAsStream('issue_updated_with_comment.json').text
    }

    String createIssueStatusUpdatedEvent() {
        this.class.getResourceAsStream('issue_updated_status_updated.json').text
    }

    String createCloudCommentAddedEvent() {
        this.class.getResourceAsStream('cloud_comment_added.json').text
    }

    def 'Should fire changelog created event when status field is updated'() {
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
                new ChangelogItem(FieldType.JIRA, 'resolution', '1', 'Fixed', '10000', 'Done'),
                new ChangelogItem(FieldType.JIRA, 'status', '10000', 'To Do', '10001', 'Done'),
        ])
    }

    def 'Should store request parameter in context'() {
        WebhookCommentEvent commentEvent = null

        given:
        def listener = Mock(JiraWebhookListener)
        def staplerRequest = Mock(StaplerRequest)
        staplerRequest.getParameter('user_id') >> 'adminId'
        staplerRequest.getParameter('user_key') >> 'adminKey'
        JiraWebhook jiraWebhook = new JiraWebhook()
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(staplerRequest, createIssueUpdatedWithCommentEvent())

        then:
        1 * listener.commentCreated(_) >> { args -> commentEvent = args[0] }
        expect commentEvent.userId, equalTo('adminId')
        expect commentEvent.userKey, equalTo('adminKey')
    }

    def 'Should not fire comment created event when an is issue created'() {
        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueCreatedEvent())

        then:
        0 * listener.commentCreated(_)
    }

    def 'Should fire comment created event when an issue is updated with comment'() {
        WebhookCommentEvent commentEvent = null

        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueUpdatedWithCommentEvent())

        then:
        def expectedDateTime = new DateTime(2015, 12, 20, 18, 25, 9, 582, UTC)
        1 * listener.commentCreated(_) >> { args -> commentEvent = args[0] }
        expect commentEvent.comment.body, is('comment body')
        expect commentEvent.comment.author.name, is('admin')
        expect commentEvent.webhookEventType, is(JiraWebhook.ISSUE_UPDATED_WEBHOOK_EVENT)
        expect commentEvent.issue.creationDate.toDateTime(UTC), is(expectedDateTime)
        expect commentEvent.issue.updateDate.toDateTime(UTC), is(expectedDateTime)
    }

    def 'Should not fire comment created event when an issue is updated without comments'() {
        given:
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createIssueUpdatedEvent())

        then:
        0 * listener.commentCreated(_)
    }

    def 'Should fire comment created event when a comment is added in JIRA Cloud'() {
        given:
        WebhookCommentEvent commentEvent = null
        JiraWebhook jiraWebhook = new JiraWebhook()

        def listener = Mock(JiraWebhookListener)
        jiraWebhook.setJiraWebhookListener(listener)

        when:
        jiraWebhook.processEvent(Mock(StaplerRequest), createCloudCommentAddedEvent())

        then:
        def expectedDateTime = new DateTime(1980, 1, 1, 0, 0, 0, 0, UTC)
        1 * listener.commentCreated(_) >> { args -> commentEvent = args[0] }
        expect commentEvent.comment.body, is('comment body')
        expect commentEvent.comment.author.name, is('admin')
        expect commentEvent.webhookEventType, is(JiraWebhook.COMMENT_CREATED_WEBHOOK_EVENT)
        expect commentEvent.issue.creationDate.toDateTime(UTC), is(expectedDateTime)
        expect commentEvent.issue.updateDate.toDateTime(UTC), is(expectedDateTime)
    }
}
