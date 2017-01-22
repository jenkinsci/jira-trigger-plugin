package com.ceilfors.jenkins.plugins.jiratrigger.ui

import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput
import com.gargoylesoftware.htmlunit.html.HtmlForm
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput
import com.gargoylesoftware.htmlunit.html.HtmlTextInput

/**
 * @author ceilfors
 */
class JiraTriggerGlobalConfigurationPage {

    private final HtmlPage configPage

    JiraTriggerGlobalConfigurationPage(HtmlPage configPage) {
        this.configPage = configPage
    }

    void save() {
        HtmlForm form = configPage.getFormByName('config')
        form.submit((HtmlButton) (form.getHtmlElementsByTagName('button')).last())
    }

    void setRootUrl(String rootUrl) {
        jiraRootUrl.setValueAttribute(rootUrl)
    }

    void setCredentials(String username, String password) {
        jiraPassword.setValueAttribute(password)
        jiraUsername.setValueAttribute(username)
    }

    def setJiraCommentReply(boolean active) {
        jiraCommentReplyCheckBox.setChecked(active)
    }

    private HtmlPasswordInput getJiraPassword() {
        throwIfNotFound('jiraPassword') {
            configPage.getFirstByXPath('//input[contains(@name, "jiraPassword")]')
        }
    }

    private HtmlTextInput getJiraUsername() {
        throwIfNotFound('jiraUsername') {
            configPage.getFirstByXPath('//input[contains(@name, "jiraUsername")]')
        }
    }

    private HtmlTextInput getJiraRootUrl() {
        throwIfNotFound('jiraRootUrl') {
            configPage.getFirstByXPath('//input[contains(@name, "jiraRootUrl")]')
        }
    }

    private HtmlCheckBoxInput getJiraCommentReplyCheckBox() {
        throwIfNotFound('jiraCommentReply') {
            configPage.getFirstByXPath('//input[contains(@name, "jiraCommentReply")]')
        }
    }

    private static <T> T throwIfNotFound(String hint, Closure<T> closure) {
        T result = closure.call()
        if (result) {
            return result
        }
        throw new RuntimeException("Couldn't find $hint")
    }
}
