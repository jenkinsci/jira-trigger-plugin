package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraChangelogTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.CustomFieldChangelogMatcher
import com.ceilfors.jenkins.plugins.jiratrigger.changelog.JiraFieldChangelogMatcher
import hudson.model.FreeStyleProject

/**
 * @author ceilfors
 */
class JiraChangelogTriggerProject extends JiraTriggerProject {

    JiraChangelogTriggerProject(FreeStyleProject project) {
        super(project)
    }

    @Override
    JiraChangelogTrigger getJiraTrigger() {
        project.getTrigger(JiraChangelogTrigger)
    }

    void addJiraFieldChangelogMatcher(String fieldId, String oldValue, String newValue) {
        def matcher = new JiraFieldChangelogMatcher(fieldId, newValue, oldValue)
        matcher.comparingNewValue = newValue != null && !newValue.empty
        matcher.comparingOldValue = oldValue != null && !oldValue.empty
        jiraTrigger.changelogMatchers.add(matcher)
        project.save()
    }

    void addCustomFieldChangelogMatcher(String fieldName, String oldValue, String newValue) {
        def matcher = new CustomFieldChangelogMatcher(fieldName, newValue, oldValue)
        matcher.comparingNewValue = newValue != null && !newValue.empty
        matcher.comparingOldValue = oldValue != null && !oldValue.empty
        jiraTrigger.changelogMatchers.add(matcher)
        project.save()
    }
}
