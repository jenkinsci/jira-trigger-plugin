package com.ceilfors.jenkins.plugins.jirabuilder.webhook
import hudson.Extension
import hudson.security.csrf.CrumbExclusion

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.logging.Logger
/**
 * @author ceilfors
 */
@Extension
class JiraWebhookCrumbExclusion extends CrumbExclusion {

    private static final Logger LOGGER = Logger.getLogger(JiraWebhookCrumbExclusion.name)

    @Override
    boolean process(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String pathInfo = request.getPathInfo()
        if (pathInfo != null && pathInfo.equals(getExclusionPath())) {
            chain.doFilter(request, response)
            return true
        } else {
            if (pathInfo.contains(JiraWebhook.URLNAME)) {
                LOGGER.finest("Ignoring pathInfo $pathInfo even when it contains $JiraWebhook.URLNAME")
            }
            return false
        }
    }

    String getExclusionPath() {
        return "/" + JiraWebhook.URLNAME + "/"
    }
}
