package com.ceilfors.jenkins.plugins.jiratrigger.changelog

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.ChangelogItem
import com.atlassian.jira.rest.client.api.domain.FieldType
import spock.lang.Specification

/**
 * @author ceilfors
 */
class ChangelogMatcherTest extends Specification {

    private class BasicChangelogMatcher extends ChangelogMatcher {

        BasicChangelogMatcher(FieldType fieldType, String field, String newValue, String oldValue,
                              boolean comparingNewValue, boolean comparingOldValue) {
            super(fieldType, field, newValue, oldValue, comparingNewValue, comparingOldValue)
        }
    }

    def "Should compare field value"(String fieldId, String matcherField, boolean result) {
        given:
        ChangelogGroup changelogGroup = new ChangelogGroup(null, null, [
                new ChangelogItem(FieldType.JIRA, fieldId, "", "", "", "")
        ])

        when:
        def matcher = new BasicChangelogMatcher(FieldType.JIRA, matcherField, "", "", false, false)

        then:
        matcher.matches(changelogGroup) == result

        where:
        fieldId       | matcherField  | result
        "status"      | "status"      | true
        "status"      | "description" | false
        "description" | "status"      | false
        "description" | "description" | true
    }

    def "Should handle multiple changelog item"(List<String> fieldIds, boolean result) {
        given:
        ChangelogGroup changelogGroup = new ChangelogGroup(null, null, fieldIds.collect {
            new ChangelogItem(FieldType.JIRA, it, null, null, null, null)
        })

        when:
        def matcher = new BasicChangelogMatcher(FieldType.JIRA, "status", "", "", false, false)

        then:
        matcher.matches(changelogGroup) == result

        where:
        fieldIds                             | result
        ["status", "description"]            | true
        ["description", "status"]            | true
        ["description", "summary"]           | false
        ["description", "status", "summary"] | true
    }

    def "Should compare new value when comparingNewValue flag is on"(
            String newValue, String matcherNewValue, boolean result) {
        given:
        ChangelogGroup changelogGroup = new ChangelogGroup(null, null, [
                new ChangelogItem(FieldType.JIRA, "status", "", "", "", newValue)
        ])

        when:
        def matcher = new BasicChangelogMatcher(FieldType.JIRA, "status", matcherNewValue, "", true, false)

        then:
        matcher.matches(changelogGroup) == result

        where:
        newValue    | matcherNewValue | result
        "new value" | "new value"     | true
        "new value" | "NEW VALUE"     | true
        "2"         | "1"             | false
        "@@"        | "!!"            | false
        ""          | ""              | true
        null        | ""              | true
    }

    def "Should not compare new value when comparingNewValue flag is off"(
            String newValue, String matcherNewValue, boolean result) {
        given:
        ChangelogGroup changelogGroup = new ChangelogGroup(null, null, [
                new ChangelogItem(FieldType.JIRA, "status", "", "", "", newValue)
        ])

        when:
        def matcher = new BasicChangelogMatcher(FieldType.JIRA, "status", matcherNewValue, "", false, false)

        then:
        matcher.matches(changelogGroup)

        where:
        newValue | matcherNewValue | result
        "2"      | "1"             | true
        "@@"     | "!!"            | true
    }

    def "Should compare old value when comparingOldValue flag is on"(
            String oldValue, String matcherOldValue, boolean result) {
        given:
        ChangelogGroup changelogGroup = new ChangelogGroup(null, null, [
                new ChangelogItem(FieldType.JIRA, "status", "", oldValue, "", "")
        ])

        when:
        def matcher = new BasicChangelogMatcher(FieldType.JIRA, "status", "", matcherOldValue, false, true)

        then:
        matcher.matches(changelogGroup) == result

        where:
        oldValue    | matcherOldValue | result
        "old value" | "old value"     | true
        "old value" | "OLD VALUE"     | true
        "2"         | "1"             | false
        "@@"        | "!!"            | false
        ""          | ""              | true
        null        | ""              | true
    }

    def "Should not compare old value when comparingOldValue flag is off"(
            String oldValue, String matcherOldValue, boolean result) {
        given:
        ChangelogGroup changelogGroup = new ChangelogGroup(null, null, [
                new ChangelogItem(FieldType.JIRA, "status", "", oldValue, "", "")
        ])

        when:
        def matcher = new BasicChangelogMatcher(FieldType.JIRA, "status", "", matcherOldValue, false, false)

        then:
        matcher.matches(changelogGroup) == result

        where:
        oldValue | matcherOldValue | result
        "2"      | "1"             | true
        "@@"     | "!!"            | true
    }
}
