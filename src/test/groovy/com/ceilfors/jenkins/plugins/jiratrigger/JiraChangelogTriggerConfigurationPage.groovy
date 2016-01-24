package com.ceilfors.jenkins.plugins.jiratrigger

import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTextInput
import hudson.triggers.Trigger
/**
 * @author ceilfors
 */
class JiraChangelogTriggerConfigurationPage extends JiraTriggerConfigurationPage {

    JiraChangelogTriggerConfigurationPage(HtmlPage configPage) {
        super(configPage)
    }

    @Override
    protected Class<? extends Trigger> getTriggerType() {
        return JiraChangelogTrigger
    }

    void setJqlFilter(String jqlFilter) {
        jqlFilterText.setValueAttribute(jqlFilter)
    }

    private HtmlTextInput getJqlFilterText() {
        getField("jqlFilter")
    }

}
