package com.ceilfors.jenkins.plugins.jiratrigger.jira

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject

/**
 * @author ceilfors
 */
class WebhookJsonParser implements JsonObjectParser<Webhook> {

    @Override
    Webhook parse(JSONObject json) throws JSONException {
        new Webhook(
                selfUri: JsonParseUtil.getSelfUri(json),
                name: json.getString('name'),
                url: json.getString('url'),
        )
    }
}
