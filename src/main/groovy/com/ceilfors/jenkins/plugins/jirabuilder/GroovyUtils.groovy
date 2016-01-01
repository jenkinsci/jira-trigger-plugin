package com.ceilfors.jenkins.plugins.jirabuilder

/**
 * @author ceilfors
 */
@Deprecated
class GroovyUtils {

    /**
     * Resolves nested property from a String.
     *
     * @param object the object which property to be resolved
     * @param property
     * @return the resolved property, null otherwise
     */
    static def resolveProperty(object, String property) {
        try {
            Eval.x(object, 'x.' + property)
        } catch (NullPointerException e) {
            null
        }
    }
}
