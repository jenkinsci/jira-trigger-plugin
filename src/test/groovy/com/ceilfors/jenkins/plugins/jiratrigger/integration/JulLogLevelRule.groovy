package com.ceilfors.jenkins.plugins.jiratrigger.integration

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import java.util.logging.*

/**
 * @author ceilfors
 */
class JulLogLevelRule implements TestRule {

    public JulLogLevelRule() {
    }

    @Override
    Statement apply(Statement base, Description description) {
        Logger topLogger = Logger.getLogger("");

        Handler consoleHandler = topLogger.handlers.find { it instanceof ConsoleHandler }
        if (!consoleHandler) {
            consoleHandler = new ConsoleHandler();
            topLogger.addHandler(consoleHandler);
        }

        consoleHandler.setLevel(Level.FINEST)

        // Required when all of the test cases is in acceptance tests without @Ignore or @IgnoreRest
        configureLog()
        return base
    }

    public static void configureLog() {
        Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF)
        Logger.getLogger("com.ceilfors.jenkins.plugins").setLevel(Level.FINEST)
    }
}
