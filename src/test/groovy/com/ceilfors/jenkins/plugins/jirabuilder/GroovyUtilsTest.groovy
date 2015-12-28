package com.ceilfors.jenkins.plugins.jirabuilder

import spock.lang.Specification

/**
 * @author ceilfors
 */
class GroovyUtilsTest extends Specification {

    def "Should resolve nested property"() {
        given:
        def object = [
                first: [
                        second: [
                                foo: "foo",
                                bar: 1,
                                boo: 2.0
                        ]
                ]
        ]

        when:
        def result = GroovyUtils.resolveProperty(object, property)

        then:
        result == expected

        where:
        property | expected
        "first.second.foo" | "foo"
        "first.second.bar" | 1
        "first.second.boo" | 2.0
    }
}
