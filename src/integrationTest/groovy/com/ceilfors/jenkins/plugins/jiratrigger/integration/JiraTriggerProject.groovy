package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.CustomFieldParameterMapping
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import hudson.model.FreeStyleProject

/**
 * @author ceilfors
 */
abstract class JiraTriggerProject {

    protected FreeStyleProject project

    protected JiraTriggerProject(FreeStyleProject project) {
        this.project = project
    }

    void addParameterMapping(String jenkinsParameter, String issueAttributePath) {
        jiraTrigger.parameterMappings.add(new IssueAttributePathParameterMapping(jenkinsParameter, issueAttributePath))
        project.save()
    }

    void addCustomFieldParameterMapping(String jenkinsParameter, String customFieldId) {
        jiraTrigger.parameterMappings.add(new CustomFieldParameterMapping(jenkinsParameter, customFieldId))
        project.save()
    }

    void setJqlFilter(String jqlFilter) {
        jiraTrigger.jqlFilter = jqlFilter
        project.save()
    }

    String getAbsoluteUrl() {
        project.absoluteUrl
    }

    abstract JiraTrigger getJiraTrigger()
}
