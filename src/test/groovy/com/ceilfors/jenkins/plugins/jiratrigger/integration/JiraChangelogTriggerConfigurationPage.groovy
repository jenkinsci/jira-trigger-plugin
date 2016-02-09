package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.CustomFieldChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.JiraFieldChangelogMatcher
import com.gargoylesoftware.htmlunit.html.*
import hudson.triggers.Trigger

/**
 * @author ceilfors
 */
class JiraChangelogTriggerConfigurationPage extends JiraTriggerConfigurationPage {

    JiraChangelogTriggerConfigurationPage(HtmlPage configPage) {
        super(configPage)
    }

    @Override
    protected Class<? extends Trigger> getTriggerType() {
        return JiraChangelogTrigger
    }

    void addJiraFieldChangelogMatcher(String fieldId, String oldValue, String newValue) {
        HtmlButton addButton = getFirstByXPath(configPage, "add changelog matcher button", '//button[contains(@suffix, "changelogMatchers")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = getFirstByXPath(parameterMappingDiv, "jira field changelog matcher button", "//a[contains(text(), '${JiraFieldChangelogMatcher.JiraFieldChangelogMatcherDescriptor.DISPLAY_NAME}')]")
        attribute.click()

        lastFieldText.setValueAttribute(fieldId)
        lastNewValueText.setValueAttribute(newValue)
        lastOldValueText.setValueAttribute(oldValue)
    }

    def addCustomFieldChangelogMatcher(String fieldName, String oldValue, String newValue) {
        HtmlButton addButton = getFirstByXPath(configPage, "add changelog matcher button", '//button[contains(@suffix, "changelogMatchers")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = getFirstByXPath(parameterMappingDiv, "custom field changelog matcher button", "//a[contains(text(), '${CustomFieldChangelogMatcher.CustomFieldChangelogMatcherDescriptor.DISPLAY_NAME}')]")
        attribute.click()

        lastFieldText.setValueAttribute(fieldName)
        lastNewValueText.setValueAttribute(newValue)
        lastOldValueText.setValueAttribute(oldValue)
    }

    private HtmlTextInput getLastFieldText() {
        getLastByXPath("field", '//input[contains(@name, "field")]')
    }

    private HtmlTextInput getLastNewValueText() {
        getLastByXPath("newValue", '//input[contains(@name, "newValue")]')
    }

    private HtmlTextInput getLastOldValueText() {
        getLastByXPath("newValue", '//input[contains(@name, "oldValue")]')
    }
}
