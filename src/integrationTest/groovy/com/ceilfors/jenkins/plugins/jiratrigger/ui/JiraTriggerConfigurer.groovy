package com.ceilfors.jenkins.plugins.jiratrigger.ui

import jenkins.model.Jenkins
import org.jvnet.hudson.test.JenkinsRule
/**
 * @author ceilfors
 */
abstract class JiraTriggerConfigurer {

    protected JenkinsRule jenkinsRule
    protected Jenkins jenkins
    protected String jobName

    protected JiraTriggerConfigurer(JenkinsRule jenkinsRule, String jobName) {
        this.jenkinsRule = jenkinsRule
        this.jenkins = jenkinsRule.instance
        this.jobName = jobName
    }

    void addParameterMapping(String jenkinsParameter, String issueAttributePath) {
        JiraTriggerConfigurationPage configPage = configure()
        configPage.addParameterMapping(jenkinsParameter, issueAttributePath)
        configPage.save()
    }

    void activate() {
        JiraTriggerConfigurationPage configPage = configure()
        configPage.activate()
        configPage.save()
    }

    abstract JiraTriggerConfigurationPage configure()
}
