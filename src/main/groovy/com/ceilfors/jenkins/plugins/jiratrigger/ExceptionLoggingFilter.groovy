package com.ceilfors.jenkins.plugins.jiratrigger

import groovy.util.logging.Log
import org.apache.commons.lang.exception.ExceptionUtils

import javax.servlet.*
import java.util.logging.Level

/**
 * @author ceilfors
 */
@Log
class ExceptionLoggingFilter implements Filter {

    @Override
    void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response)
        } catch (Throwable e) {
            def rootCause = ExceptionUtils.getRootCause(e)
            if (rootCause instanceof JiraTriggerException) {
                logException(rootCause)
            }
            throw e
        }
    }

    private static void logException(JiraTriggerException e) {
        if (e.errorCode == JiraTriggerErrorCode.JIRA_NOT_CONFIGURED) {
            log.severe("JIRA is not configured in Jenkins Global Settings. Please set the ${e.attributes['config']}.")
        } else {
            log.log(Level.SEVERE, "Hit JiraTriggerException! (jira-trigger-plugin has failed to translate this exception to a human friendly error message, please report a bug).", e)
        }
    }

    @Override
    void destroy() {
    }
}
