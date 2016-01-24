package com.ceilfors.jenkins.plugins.jiratrigger.webhook
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.internal.json.ChangelogItemJsonParser
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser
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
    private final IssueJsonParser issueJsonParser = new IssueJsonParser(new JSONObject([:]), new JSONObject([:]))

    @Override
    WebhookChangelogEvent parse(JSONObject json) throws JSONException {
        def items = JsonParseUtil.parseJsonArray(json.getJSONObject("changelog").getJSONArray("items"), changelogItemJsonParser)
        def issue = json.getJSONObject("issue")
        issue.put("expand", "") // Webhook event doesn't have expand
        new WebhookChangelogEvent(
                json.getLong("timestamp"),
                json.getString("webhookEvent"),
                issueJsonParser.parse(issue),
                new ChangelogGroup(null, null, items)
        )
    }
}
