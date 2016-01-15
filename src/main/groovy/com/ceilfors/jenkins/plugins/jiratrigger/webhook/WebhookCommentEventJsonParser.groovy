package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import com.atlassian.jira.rest.client.internal.json.CommentJsonParser
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject
/**
 * @author ceilfors
 */
class WebhookCommentEventJsonParser implements JsonObjectParser<WebhookCommentEvent> {

    @Override
    WebhookCommentEvent parse(JSONObject json) throws JSONException {
        new WebhookCommentEvent(
                json.getLong("timestamp"),
                json.getString("webhookEvent"),
                new CommentJsonParser().parse(json.getJSONObject("comment"))
        )
    }
}
