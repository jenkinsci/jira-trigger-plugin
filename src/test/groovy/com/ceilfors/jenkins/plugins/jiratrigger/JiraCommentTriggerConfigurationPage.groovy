package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import com.gargoylesoftware.htmlunit.html.*
import hudson.triggers.Trigger
/**
 * @author ceilfors
 */
class JiraCommentTriggerConfigurationPage extends JiraTriggerConfigurationPage {

    JiraCommentTriggerConfigurationPage(HtmlPage configPage) {
        super(configPage)
    }

    @Override
    protected Class<? extends Trigger> getTriggerType() {
        return JiraCommentTrigger
    }

    void setCommentPattern(String commentPattern) {
        commentPatternText.setValueAttribute(commentPattern)
    }

    void setJqlFilter(String jqlFilter) {
        jqlFilterText.setValueAttribute(jqlFilter)
    }

    String getCommentPattern() {
        commentPatternText.valueAttribute
    }

    void addParameterMapping(String jenkinsParameter, String attributePath) {
        HtmlButton addButton = getFirstByXPath(configPage, "add parameter mapping button", '//button[contains(@suffix, "parameterMappings")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = getFirstByXPath(parameterMappingDiv, "issue attribute path parameter button", "//a[contains(text(), '${IssueAttributePathParameterMapping.IssueAttributePathParameterMappingDescriptor.DISPLAY_NAME}')]")
        attribute.click()

        lastJenkinsParameterText.setValueAttribute(jenkinsParameter)
        lastAttributePathText.setValueAttribute(attributePath)
    }

    private HtmlTextInput getLastJenkinsParameterText() {
        getLastByXPath("jenkinsParameter", '//input[contains(@name, "jenkinsParameter")]')
    }

    private HtmlTextInput getLastAttributePathText() {
        getLastByXPath("attributePath", '//input[contains(@name, "issueAttributePath")]')
    }

    private HtmlTextInput getCommentPatternText() {
        getField("commentPattern")
    }

    private HtmlTextInput getJqlFilterText() {
        getField("jqlFilter")
    }

}
