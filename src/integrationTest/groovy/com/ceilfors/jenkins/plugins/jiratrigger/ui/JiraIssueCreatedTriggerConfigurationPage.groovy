package com.ceilfors.jenkins.plugins.jiratrigger.ui

import com.ceilfors.jenkins.plugins.jiratrigger.JiraIssueCreatedTrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.triggers.Trigger
/**
 * @author ceilfors
 */
class JiraIssueCreatedTriggerConfigurationPage extends JiraTriggerConfigurationPage {

    JiraIssueCreatedTriggerConfigurationPage(HtmlPage configPage) {
        super(configPage)
    }

    @Override
    protected Class<? extends Trigger> getTriggerType() {
        JiraIssueCreatedTrigger
    }
}
