package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import com.gargoylesoftware.htmlunit.html.*
import hudson.triggers.Trigger
/**
 * @author ceilfors
 */
abstract class JiraTriggerConfigurationPage {

    protected HtmlPage configPage

    JiraTriggerConfigurationPage(HtmlPage configPage) {
        this.configPage = configPage
    }

    void save() {
        HtmlForm form = configPage.getFormByName("config")
        form.submit((HtmlButton) (form.getHtmlElementsByTagName("button")).last())
        configPage.cleanUp()
    }


    void addParameterMapping(String jenkinsParameter, String attributePath) {
        HtmlButton addButton = getFirstByXPath(configPage, "add parameter mapping button", '//button[contains(@suffix, "parameterMappings")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = getFirstByXPath(parameterMappingDiv, "issue attribute path parameter button", "//a[contains(text(), '${IssueAttributePathParameterMapping.IssueAttributePathParameterMappingDescriptor.DISPLAY_NAME}')]")
        attribute.click()
        configPage.webClient.waitForBackgroundJavaScriptStartingBefore(1000)

        lastJenkinsParameterText.setValueAttribute(jenkinsParameter)
        lastAttributePathText.setValueAttribute(attributePath)
    }

    void setJqlFilter(String jqlFilter) {
        jqlFilterText.setValueAttribute(jqlFilter)
    }

    protected HtmlTextInput getJqlFilterText() {
        getField("jqlFilter")
    }

    protected HtmlTextInput getLastJenkinsParameterText() {
        getLastByXPath("jenkinsParameter", '//input[contains(@name, "jenkinsParameter")]')
    }

    protected HtmlTextInput getLastAttributePathText() {
        getLastByXPath("attributePath", '//input[contains(@name, "issueAttributePath")]')
    }

    public void activate() {
        triggerCheckBox.setChecked(true)
    }

    protected static <T> T throwIfNotFound(String hint, Closure<T> closure) {
        T result = closure.call()
        if (result) {
            return result
        } else {
            throw new RuntimeException("Couldn't find $hint")
        }
    }

    protected <T> T getField(String fieldName) {
        getFirstByXPath(fieldName, "//input[contains(@name, '${fieldName}')]")
    }

    protected <T> T getFirstByXPath(String hint, xpathExpr) {
        getFirstByXPath(configPage, hint, xpathExpr)
    }

    protected <T> T getLastByXPath(String hint, xpathExpr) {
        getLastByXPath(configPage, hint, xpathExpr)
    }

    protected <T> T getFirstByXPath(DomNode node, String hint, xpathExpr) {
        throwIfNotFound(hint) {
            node.getFirstByXPath("//tr[@nameref='${getNameref()}']${xpathExpr}")
        }
    }

    protected <T> T getLastByXPath(DomNode node, String hint, xpathExpr) {
        throwIfNotFound(hint) {
            node.<T>getByXPath("//tr[@nameref='${getNameref()}']${xpathExpr}").last()
        }
    }

    protected HtmlCheckBoxInput getTriggerCheckBox() {
        throwIfNotFound("triggerCheckBox-${triggerType.simpleName}") {
            configPage.getFirstByXPath("""//input[contains(@name, "${triggerType.simpleName}")]""")
        }
    }

    protected String getNameref() {
        triggerCheckBox.getAttribute("id")
    }

    protected abstract Class<? extends Trigger> getTriggerType()
}
