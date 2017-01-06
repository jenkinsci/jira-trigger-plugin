package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.ceilfors.jenkins.plugins.jiratrigger.JiraTrigger
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import hudson.model.FreeStyleProject

/**
 * @author ceilfors
 */
abstract class JiraTriggerProject {

    protected FreeStyleProject project

    JiraTriggerProject(FreeStyleProject project) {
        this.project = project
    }

    void addParameterMapping(String jenkinsParameter, String issueAttributePath) {
        def trigger = getJiraTrigger()
        if (trigger == null) {
            throw new IllegalStateException('Trigger was null in CI?')
        } else {
            System.err.println("Trigger was successfully retrieved")
        }
        jiraTrigger.parameterMappings.add(new IssueAttributePathParameterMapping(jenkinsParameter, issueAttributePath))
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
