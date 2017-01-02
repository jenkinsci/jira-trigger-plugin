package com.ceilfors.jenkins.plugins.jiratrigger.ui
import com.gargoylesoftware.htmlunit.html.*
/**
 * @author ceilfors
 */
class JiraTriggerGlobalConfigurationPage {

    private HtmlPage configPage

    JiraTriggerGlobalConfigurationPage(HtmlPage configPage) {
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

    def setJiraCommentReply(boolean active) {
        getJiraCommentReplyCheckBox().setChecked(active)
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

    private HtmlCheckBoxInput getJiraCommentReplyCheckBox() {
        throwIfNotFound("jiraCommentReply") {
            configPage.getFirstByXPath('//input[contains(@name, "jiraCommentReply")]')
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
