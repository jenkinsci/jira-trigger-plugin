package com.ceilfors.jenkins.plugins.jirabuilder.jira

/**
 * @author ceilfors
 */
class WebhookInput {

    String url
    String name
    List<String> events
    String jqlFilter
}
