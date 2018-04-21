package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import org.codehaus.jettison.json.JSONObject

/**
 * @author ceilfors
 */
class WebhookJsonParserUtils {

    /**
     * Fills details needed by JRC JSON Parser that are missing in Webhook events.
     */
    static void satisfyRequiredKeys(JSONObject webhookEvent) {
        JSONObject issue = webhookEvent.getJSONObject('issue')
        putIfAbsent(issue, 'expand', '')
    }

    static void putIfAbsent(JSONObject jsonObject, String key, Object value) {
        if (!jsonObject.opt(key)) {
            jsonObject.put(key, value)
        }
    }
}
