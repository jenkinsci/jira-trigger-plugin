package com.ceilfors.jenkins.plugins.jirabuilder

import hudson.model.Cause

/**
 * @author ceilfors
 */
class JiraBuilderCause extends Cause {

    @Override
    String getShortDescription() {
        return "Jira Builder"
    }
}
