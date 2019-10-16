package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraIssueCreatedTrigger
import hudson.model.FreeStyleProject

/**
 * @author ceilfors
 */
class JiraIssueCreatedTriggerProject extends JiraTriggerProject {

    JiraIssueCreatedTriggerProject(FreeStyleProject project) {
        super(project)
    }

    @Override
    JiraIssueCreatedTrigger getJiraTrigger() {
        project.getTrigger(JiraIssueCreatedTrigger)
    }
}
