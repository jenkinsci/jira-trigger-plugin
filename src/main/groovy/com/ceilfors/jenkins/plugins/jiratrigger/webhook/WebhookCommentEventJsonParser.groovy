package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.internal.json.CommentJsonParser
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject

/**
 * @author ceilfors
 */
class WebhookCommentEventJsonParser implements JsonObjectParser<WebhookCommentEvent> {

    private final IssueJsonParser issueJsonParser = new IssueJsonParser(new JSONObject([:]), new JSONObject([:]))

    @Override
    WebhookCommentEvent parse(JSONObject json) throws JSONException {
        JSONObject issue = json.getJSONObject('issue')
        issue.put('expand', '') // Webhook event doesn't have expand
        new WebhookCommentEvent(
                json.getLong('timestamp'),
                json.getString('webhookEvent'),
                issueJsonParser.parse(issue),
                new CommentJsonParser().parse(json.getJSONObject('comment'))
        )
    }
}
