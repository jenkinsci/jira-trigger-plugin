package com.ceilfors.jenkins.plugins.jirabuilder

import com.ceilfors.jenkins.plugins.jirabuilder.parameter.AttributePathParameterMapping
import com.gargoylesoftware.htmlunit.html.*

/**
 * @author ceilfors
 */
class JiraBuilderConfigurePage {

    private HtmlPage configPage

    JiraBuilderConfigurePage(HtmlPage configPage) {
        this.configPage = configPage
    }

    void activateJiraBuilderTrigger() {
        jiraBuilderTriggerCheckBox.setChecked(true)
    }

    void setCommentPattern(String commentPattern) {
        commentPatternText.setValueAttribute(commentPattern)
    }

    void addParameterMapping(String jenkinsParameter, String attributePath) {
        HtmlButton addButton = configPage.getFirstByXPath('//button[contains(@suffix, "parameterMappings")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = parameterMappingDiv.getFirstByXPath("""//a[contains(text(), "Attribute Path")]""")
        attribute.click()

        lastJenkinsParameterText.setValueAttribute(jenkinsParameter)
        lastAttributePathText.setValueAttribute(attributePath)
    }

    void save() {
        HtmlForm form = configPage.getFormByName("config")
        form.submit((HtmlButton) (form.getHtmlElementsByTagName("button")).last())
    }

    private HtmlTextInput getLastJenkinsParameterText() {
        def parameterMappingDiv = lastParameterMappingDiv
        throwIfNotFound("jenkinsParameter in last parameterMapping div") {
            parameterMappingDiv.getFirstByXPath('//input[contains(@name, "jenkinsParameter")]')
        }
    }

    private HtmlTextInput getLastAttributePathText() {
        def parameterMappingDiv = lastParameterMappingDiv
        throwIfNotFound("attributePath in last parameterMapping div") {
            parameterMappingDiv.getFirstByXPath('//input[contains(@name, "jiraAttributePath")]')
        }
    }

    private HtmlDivision getLastParameterMappingDiv() {
        configPage.getByXPath("""//div[contains(@descriptorId, "${AttributePathParameterMapping.simpleName}")]""").last()
    }

    private HtmlCheckBoxInput getJiraBuilderTriggerCheckBox() {
        throwIfNotFound("jiraBuilderTriggerCheckBox") {
            configPage.getFirstByXPath("""//input[contains(@name, "${JiraBuilderTrigger.simpleName}")]""")
        }
    }

    private HtmlTextInput getCommentPatternText() {
        throwIfNotFound("commentPatternText") {
            configPage.getFirstByXPath('//input[contains(@name, "commentPattern")]')
        }
    }

    private static <T> T throwIfNotFound(String hint, Closure<T> closure) {
        T result = closure.call()
        if (result) {
            return result
        } else {
            throw new RuntimeException("Couldn't find $hint")
        }
    }
}
