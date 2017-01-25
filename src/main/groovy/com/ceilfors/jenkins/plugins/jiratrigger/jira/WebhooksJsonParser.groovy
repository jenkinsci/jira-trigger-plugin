package com.ceilfors.jenkins.plugins.jiratrigger.jira
import com.atlassian.jira.rest.client.internal.json.JsonArrayParser
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONException
/**
 * @author ceilfors
 */
class WebhooksJsonParser implements JsonArrayParser<Collection<Webhook>> {

    @Override
    Collection<Webhook> parse(JSONArray json) throws JSONException {
        JsonParseUtil.parseJsonArray(json, new WebhookJsonParser())
    }
}
