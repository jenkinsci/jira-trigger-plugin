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
        def trigger = project.getTrigger(JiraCommentTrigger)
        if (trigger == null) {
            throw new IllegalStateException('Trigger was null in CI?')
        } else {
            System.err.println("Trigger was successfully retrieved")
        }
        return trigger
    }

    void setCommentPattern(String commentPattern) {
        jiraTrigger.setCommentPattern(commentPattern)
        project.save()
    }
}
