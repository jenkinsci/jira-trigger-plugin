package com.ceilfors.jenkins.plugins.jiratrigger.ui

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
        JiraCommentTrigger
    }

    void setCommentPattern(String commentPattern) {
        commentPatternText.setValueAttribute(commentPattern)
    }

    private HtmlTextInput getCommentPatternText() {
        getField('commentPattern')
    }
}
