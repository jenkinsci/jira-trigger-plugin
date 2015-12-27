package com.ceilfors.jenkins.plugins.jirabuilder

import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput
import com.gargoylesoftware.htmlunit.html.HtmlForm
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTextInput

/**
 * @author ceilfors
 */
class JiraBuilderConfigurePage {

    private HtmlPage configPage

    JiraBuilderConfigurePage(HtmlPage configPage) {
        this.configPage = configPage
    }

    HtmlCheckBoxInput getTrigger() {
        configPage.getFirstByXPath("""//input[contains(@name, "${JiraBuilderTrigger.simpleName}")]""")
    }

    HtmlTextInput getCommentPattern() {
        configPage.getFirstByXPath('//input[contains(@name, "commentPattern")]')
    }

    void save() {
        HtmlForm form = configPage.getFormByName("config")
        form.submit((HtmlButton) (form.getHtmlElementsByTagName("button")).last())
    }
}
