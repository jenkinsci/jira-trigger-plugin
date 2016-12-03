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
        addChangelogMatcher(JiraFieldChangelogMatcher.JiraFieldChangelogMatcherDescriptor.DISPLAY_NAME)
        lastFieldText.setValueAttribute(fieldId)
        setNewValue(newValue)
        setOldValue(oldValue)
    }

    def addCustomFieldChangelogMatcher(String fieldName, String oldValue, String newValue) {
        addChangelogMatcher(CustomFieldChangelogMatcher.CustomFieldChangelogMatcherDescriptor.DISPLAY_NAME)
        lastFieldText.setValueAttribute(fieldName)
        setNewValue(newValue)
        setOldValue(oldValue)
    }

    private void setNewValue(String newValue) {
        if (newValue) {
            lastComparingNewValueCheckBox.setChecked(true)
            lastNewValueText.setValueAttribute(newValue)
        } else {
            lastComparingNewValueCheckBox.setChecked(false)
        }
    }

    private void setOldValue(String oldValue) {
        if (oldValue) {
            lastComparingOldValueCheckBox.setChecked(true)
            lastOldValueText.setValueAttribute(oldValue)
        } else {
            lastComparingOldValueCheckBox.setChecked(false)
        }
    }

    private void addChangelogMatcher(String displayName) {
        HtmlButton addButton = getFirstByXPath(configPage, "add changelog matcher button", '//button[contains(@suffix, "changelogMatchers")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = getFirstByXPath(parameterMappingDiv, "custom field changelog matcher button", "//a[contains(text(), '${displayName}')]")
        attribute.click()
        configPage.webClient.waitForBackgroundJavaScriptStartingBefore(1000)
    }

    private HtmlTextInput getLastFieldText() {
        getLastByXPath("field", '//input[contains(@name, "field")]')
    }

    private HtmlTextInput getLastNewValueText() {
        getLastByXPath("newValue", '//input[contains(@name, "newValue")]')
    }

    private HtmlTextInput getLastOldValueText() {
        getLastByXPath("oldValue", '//input[contains(@name, "oldValue")]')
    }

    private HtmlCheckBoxInput getLastComparingNewValueCheckBox() {
        getLastByXPath("comparingNewValue", '//input[contains(@name, "comparingNewValue")]')
    }

    private HtmlCheckBoxInput getLastComparingOldValueCheckBox() {
        getLastByXPath("comparingOldValue", '//input[contains(@name, "comparingOldValue")]')
    }
}
