package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger
import hudson.model.FreeStyleProject
/**
 * @author ceilfors
 */
class JiraCommentTriggerProject extends JiraTriggerProject {

    JiraCommentTriggerProject(FreeStyleProject project) {
        super(project)
    }

    @Override
    JiraCommentTrigger getJiraTrigger() {
        project.getTrigger(JiraCommentTrigger)
    }

    void setCommentPattern(String commentPattern) {
        jiraTrigger.setCommentPattern(commentPattern)
        project.save()
    }
}
