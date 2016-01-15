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
            def jiraBuilderException = ExceptionUtils.getThrowableList(e).find { it instanceof JiraBuilderException }
            if (jiraBuilderException) {
                logOrRethrow(jiraBuilderException as JiraBuilderException)
            }
        }
    }

    private static void logOrRethrow(JiraBuilderException e) {
        if (e.errorCode == JiraBuilderErrorCode.JIRA_NOT_CONFIGURED) {
            log.severe("JIRA is not configured in Jenkins Global Settings. Please set the ${e.attributes['config']}.")
        } else {
            throw e
        }
    }

    @Override
    void destroy() {
    }
}
