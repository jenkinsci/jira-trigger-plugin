package com.ceilfors.jenkins.plugins.jiratrigger

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import java.util.logging.ConsoleHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author ceilfors
 */
class JulLogLevelRule implements TestRule {

    private Level pluginLogLevel

    public JulLogLevelRule(Level pluginLogLevel) {
        this.pluginLogLevel = pluginLogLevel
    }

    @Override
    Statement apply(Statement base, Description description) {
        Logger topLogger = Logger.getLogger("");

        Handler consoleHandler = topLogger.handlers.find { it instanceof ConsoleHandler }
        if (!consoleHandler) {
            consoleHandler = new ConsoleHandler();
            topLogger.addHandler(consoleHandler);
        }
        consoleHandler.setLevel(pluginLogLevel)
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF)
        Logger.getLogger("com.ceilfors.jenkins.plugins").setLevel(pluginLogLevel)
        return base
    }
}
