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
    JiraCommentTrigger getTrigger() {
        def trigger = project.getTrigger(JiraCommentTrigger)
        if (trigger == null) {
            throw new IllegalStateException('Trigger was null in CI?')
        }
        return trigger
    }

    void setCommentPattern(String commentPattern) {
        trigger.setCommentPattern(commentPattern)
        project.save()
    }
}
