package com.ceilfors.jenkins.plugins.jirabuilder
import hudson.Extension
import hudson.model.RootAction
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse
import org.kohsuke.stapler.interceptor.RequirePOST
/**
 * @author ceilfors
 */
@Extension
class JiraBuilderRootAction implements RootAction {

    @Override
    String getIconFileName() {
        return null
    }

    @Override
    String getDisplayName() {
        return "JIRA Builder"
    }

    @Override
    String getUrlName() {
        return "jirabuilder"
    }

    @RequirePOST
    public void doHello(StaplerRequest request, StaplerResponse response) {
        println getRequestBody(request)
    }

    private String getRequestBody(StaplerRequest req) {
        return req.reader.text
    }
}
