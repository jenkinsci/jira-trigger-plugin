package com.ceilfors.jenkins.plugins.jiratrigger.jira

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator
import org.codehaus.jettison.json.JSONException
import org.codehaus.jettison.json.JSONObject

/**
 * @author ceilfors
 */
class WebhookInputJsonGenerator implements JsonGenerator<WebhookInput> {

    @Override
    JSONObject generate(WebhookInput webhook) throws JSONException {
        new JSONObject()
                .put('name', webhook.name)
                .put('url', webhook.url)
                .put('events', webhook.events)
    }
}
