package com.ceilfors.jenkins.plugins.jiratrigger

import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlForm
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput
import com.gargoylesoftware.htmlunit.html.HtmlTextInput

/**
 * @author ceilfors
 */
class JiraBuilderGlobalConfigurationPage {

    private HtmlPage configPage

    JiraBuilderGlobalConfigurationPage(HtmlPage configPage) {
        this.configPage = configPage
    }

    void save() {
        HtmlForm form = configPage.getFormByName("config")
        form.submit((HtmlButton) (form.getHtmlElementsByTagName("button")).last())
    }

    public void setRootUrl(String rootUrl) {
        jiraRootUrl.setValueAttribute(rootUrl)
    }

    public void setCredentials(String username, String password) {
        jiraPassword.setValueAttribute(password)
        jiraUsername.setValueAttribute(username)
    }

    private HtmlPasswordInput getJiraPassword() {
        throwIfNotFound("jiraPassword") {
            configPage.getFirstByXPath('//input[contains(@name, "jiraPassword")]')
        }
    }

    private HtmlTextInput getJiraUsername() {
        throwIfNotFound("jiraUsername") {
            configPage.getFirstByXPath('//input[contains(@name, "jiraUsername")]')
        }
    }

    private HtmlTextInput getJiraRootUrl() {
        throwIfNotFound("jiraRootUrl") {
            configPage.getFirstByXPath('//input[contains(@name, "jiraRootUrl")]')
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
