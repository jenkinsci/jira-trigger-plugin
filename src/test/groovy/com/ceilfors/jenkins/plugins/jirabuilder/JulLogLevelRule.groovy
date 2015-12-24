package com.ceilfors.jenkins.plugins.jirabuilder

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

    private Level level

    public JulLogLevelRule(Level level) {
        this.level = level
    }

    @Override
    Statement apply(Statement base, Description description) {
        Logger topLogger = Logger.getLogger("");

        Handler consoleHandler = topLogger.handlers.find { it instanceof ConsoleHandler }
        if (!consoleHandler) {
            consoleHandler = new ConsoleHandler();
            topLogger.addHandler(consoleHandler);
        }
        consoleHandler.setLevel(level)
        Logger.getLogger("com.ceilfors.jenkins.plugins").setLevel(level)
        return base
    }
}
