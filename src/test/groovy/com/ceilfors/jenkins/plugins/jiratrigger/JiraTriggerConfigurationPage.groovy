package com.ceilfors.jenkins.plugins.jiratrigger
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import com.gargoylesoftware.htmlunit.html.*
/**
 * @author ceilfors
 */
class JiraTriggerConfigurationPage {

    private HtmlPage configPage

    JiraTriggerConfigurationPage(HtmlPage configPage) {
        this.configPage = configPage
    }

    void activateJiraCommentTrigger() {
        jiraCommentTriggerCheckBox.setChecked(true)
    }

    def activateJiraChangelogTrigger() {
        jiraChangelogTriggerCheckBox.setChecked(true)
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
        HtmlButton addButton = configPage.getFirstByXPath('//button[contains(@suffix, "parameterMappings")]')
        addButton.click()

        HtmlDivision parameterMappingDiv = addButton.parentNode.parentNode.parentNode as HtmlDivision
        HtmlAnchor attribute = parameterMappingDiv.getFirstByXPath("""//a[contains(text(), "Issue Attribute Path")]""")
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
            parameterMappingDiv.getFirstByXPath('.//input[contains(@name, "jenkinsParameter")]')
        }
    }

    private HtmlTextInput getLastAttributePathText() {
        def parameterMappingDiv = lastParameterMappingDiv
        throwIfNotFound("attributePath in last parameterMapping div") {
            parameterMappingDiv.getFirstByXPath('.//input[contains(@name, "issueAttributePath")]')
        }
    }

    private HtmlDivision getLastParameterMappingDiv() {
        configPage.getByXPath("""//div[contains(@descriptorId, "${IssueAttributePathParameterMapping.simpleName}")]""").last()
    }

    private HtmlCheckBoxInput getJiraCommentTriggerCheckBox() {
        throwIfNotFound("jiraCommentTriggerCheckBox") {
            configPage.getFirstByXPath("""//input[contains(@name, "${JiraCommentTrigger.simpleName}")]""")
        }
    }

    private HtmlCheckBoxInput getJiraChangelogTriggerCheckBox() {
        throwIfNotFound("jiraChangelogTriggerCheckBox") {
            configPage.getFirstByXPath("""//input[contains(@name, "${JiraChangelogTrigger.simpleName}")]""")
        }
    }

    private HtmlTextInput getCommentPatternText() {
        throwIfNotFound("commentPatternText") {
            configPage.getFirstByXPath('//input[contains(@name, "commentPattern")]')
        }
    }

    private HtmlTextInput getJqlFilterText() {
        throwIfNotFound("jqlFilter") {
            configPage.getFirstByXPath('//input[contains(@name, "jqlFilter")]')
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
