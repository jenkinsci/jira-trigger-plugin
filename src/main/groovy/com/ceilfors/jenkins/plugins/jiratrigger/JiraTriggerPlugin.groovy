package com.ceilfors.jenkins.plugins.jiratrigger

import hudson.Plugin
import hudson.util.PluginServletFilter

/**
 * @author ceilfors
 */
class JiraTriggerPlugin extends Plugin {

    @Override
    void start() throws Exception {
        PluginServletFilter.addFilter(new ExceptionLoggingFilter())
    }
}
