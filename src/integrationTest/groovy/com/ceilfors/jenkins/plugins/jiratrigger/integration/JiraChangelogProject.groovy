package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.CustomFieldChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.JiraFieldChangelogMatcher
import hudson.model.FreeStyleProject
/**
 * @author ceilfors
 */
class JiraChangelogProject extends JiraTriggerProject {

    JiraChangelogProject(FreeStyleProject project) {
        super(project)
    }

    @Override
    JiraChangelogTrigger getJiraTrigger() {
        project.getTrigger(JiraChangelogTrigger)
    }

    void addJiraFieldChangelogMatcher(String fieldId, String oldValue, String newValue) {
        boolean comparingOldValue = oldValue != null && !oldValue.empty
        boolean comparingNewValue = newValue != null && !newValue.empty
        jiraTrigger.changelogMatchers.add(
                new JiraFieldChangelogMatcher(fieldId, newValue, oldValue, comparingNewValue, comparingOldValue))
        project.save()
    }

    void addCustomFieldChangelogMatcher(String fieldName, String oldValue, String newValue) {
        boolean comparingOldValue = oldValue != null && !oldValue.empty
        boolean comparingNewValue = newValue != null && !newValue.empty
        jiraTrigger.changelogMatchers.add(
                new CustomFieldChangelogMatcher(fieldName, newValue, oldValue, comparingNewValue, comparingOldValue))
        project.save()
    }
}
