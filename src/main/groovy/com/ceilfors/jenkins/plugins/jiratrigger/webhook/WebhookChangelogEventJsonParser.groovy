package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.internal.json.ChangelogItemJsonParser
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject
/**
 * @author ceilfors
 */
class WebhookChangelogEventJsonParser implements JsonObjectParser<WebhookChangelogEvent> {

    /**
     * Not using ChangelogJsonParser because it is expecting "created" field which is not
     * being supplied from webhook event.
     */
    private final ChangelogItemJsonParser changelogItemJsonParser = new ChangelogItemJsonParser();

    @Override
    WebhookChangelogEvent parse(JSONObject json) throws JSONException {
        def items = JsonParseUtil.parseJsonArray(json.getJSONObject("changelog").getJSONArray("items"), changelogItemJsonParser)
        new WebhookChangelogEvent(
                json.getLong("timestamp"),
                json.getString("webhookEvent"),
                new ChangelogGroup(null, null, items)
        )
    }
}
