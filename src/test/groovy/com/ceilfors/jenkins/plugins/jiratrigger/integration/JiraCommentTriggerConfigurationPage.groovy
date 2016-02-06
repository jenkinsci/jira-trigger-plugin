package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTextInput
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

    private HtmlTextInput getCommentPatternText() {
        getField("commentPattern")
    }

    private HtmlTextInput getJqlFilterText() {
        getField("jqlFilter")
    }

}
