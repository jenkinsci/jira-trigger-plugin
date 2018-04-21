package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.internal.json.CommentJsonParser
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject

import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.putIfAbsent
import static com.ceilfors.jenkins.plugins.jiratrigger.webhook.WebhookJsonParserUtils.satisfyRequiredKeys

/**
 * @author ceilfors
 */
class WebhookCommentEventJsonParser implements JsonObjectParser<WebhookCommentEvent> {

    private static final DATE_FIELD_NOT_EXIST = '1980-01-01T00:00:00.000+0000'
    private static final ISSUE_KEY = 'issue'

    private final IssueJsonParser issueJsonParser = new IssueJsonParser(new JSONObject([:]), new JSONObject([:]))

    /**
     * Fills details needed by JRC JSON Parser that are missing in JIRA Cloud Webhook events.
     */
    private static void satisfyCloudRequiredKeys(JSONObject json) {
        JSONObject fields = json.getJSONObject(ISSUE_KEY).getJSONObject('fields')
        putIfAbsent(fields, 'created', DATE_FIELD_NOT_EXIST)
        putIfAbsent(fields, 'updated', DATE_FIELD_NOT_EXIST)
    }

    @Override
    WebhookCommentEvent parse(JSONObject json) throws JSONException {
        satisfyRequiredKeys(json)
        satisfyCloudRequiredKeys(json)

        new WebhookCommentEvent(
                json.getLong('timestamp'),
                json.getString('webhookEvent'),
                issueJsonParser.parse(json.getJSONObject(ISSUE_KEY)),
                new CommentJsonParser().parse(json.getJSONObject('comment'))
        )
    }
}
