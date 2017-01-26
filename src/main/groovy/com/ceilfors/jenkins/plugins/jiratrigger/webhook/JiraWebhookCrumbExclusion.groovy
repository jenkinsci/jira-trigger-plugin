package com.ceilfors.jenkins.plugins.jiratrigger.webhook

import hudson.Extension
import hudson.security.csrf.CrumbExclusion

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author ceilfors
 */
@Extension
class JiraWebhookCrumbExclusion extends CrumbExclusion {

    @Override
    boolean process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String pathInfo = request.getPathInfo()
        if (pathInfo != null && (pathInfo == exclusionPath || pathInfo == exclusionPath + '/')) {
            chain.doFilter(request, response)
            return true
        }
        false
    }

    String getExclusionPath() {
        "/$JiraWebhook.URL_NAME"
    }
}
