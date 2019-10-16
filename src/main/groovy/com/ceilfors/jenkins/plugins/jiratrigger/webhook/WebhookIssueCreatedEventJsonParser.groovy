package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject

import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.satisfyRequiredKeys

/**
 * @author baohaojun
 */
class WebhookIssueCreatedEventJsonParser implements JsonObjectParser<WebhookIssueCreatedEvent> {

    private final IssueJsonParser issueJsonParser = new IssueJsonParser(new JSONObject([:]), new JSONObject([:]))

    @Override
    WebhookIssueCreatedEvent parse(JSONObject webhookEvent) throws JSONException {
        satisfyRequiredKeys(webhookEvent)

        new WebhookIssueCreatedEvent(
                webhookEvent.getLong('timestamp'),
                webhookEvent.getString('webhookEvent'),
                issueJsonParser.parse(webhookEvent.getJSONObject('issue'))
        )
    }
}
