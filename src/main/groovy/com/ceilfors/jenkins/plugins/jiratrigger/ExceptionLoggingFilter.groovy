package com.ceilfors.jenkins.plugins.jiratrigger

import groovy.util.logging.Log
import org.apache.commons.lang.exception.ExceptionUtils

import javax.servlet.*

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
                log.severe(getErrorMessage(rootCause))
            }
            throw e
        }
    }

    private static String getErrorMessage(JiraTriggerException e) {
        if (e.errorCode == JiraTriggerErrorCode.JIRA_NOT_CONFIGURED) {
            "JIRA is not configured in Jenkins Global Settings. Please set the ${e.attributes['config']}."
        } else {
            ""
        }
    }

    @Override
    void destroy() {
    }
}
