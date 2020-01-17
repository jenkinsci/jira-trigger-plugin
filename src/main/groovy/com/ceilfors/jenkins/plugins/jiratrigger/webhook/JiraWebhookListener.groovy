package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import org.codehaus.jettison.json.JSONObject

/**
 * @author ceilfors
 */
interface JiraWebhookListener {

    void commentCreated(WebhookCommentEvent commentEvent, JSONObject issueJsonObject)

    void changelogCreated(WebhookChangelogEvent changelogEvent, JSONObject issueJsonObject)
    void issueCreated(WebhookIssueCreatedEvent issueCreatedEvent, JSONObject issueJsonObject)
}
