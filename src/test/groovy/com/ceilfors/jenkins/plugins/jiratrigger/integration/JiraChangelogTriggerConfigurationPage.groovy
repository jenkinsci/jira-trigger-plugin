package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.BuiltInFieldChangelogMatcher
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTextInput
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

    void setJqlFilter(String jqlFilter) {
        jqlFilterText.setValueAttribute(jqlFilter)
    }

    private HtmlTextInput getJqlFilterText() {
        getField("jqlFilter")
    }


    void addChangelogMatcher(String field, String oldValue, String newValue) {
        HtmlButton addButton = getFirstByXPath(configPage, "add changelog matcher button", '//button[contains(@suffix, "changelogMatchers")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = getFirstByXPath(parameterMappingDiv, "changelog matcher button", "//a[contains(text(), '${BuiltInFieldChangelogMatcher.BuiltInFieldChangelogMatcherDescriptor.DISPLAY_NAME}')]")
        attribute.click()

        lastFieldText.setValueAttribute(field)
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
