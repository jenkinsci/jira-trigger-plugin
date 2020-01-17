package com.ceilfors.jenkins.plugins.jiratrigger.jira

import com.atlassian.jira.rest.client.api.domain.Field
import org.codehaus.jettison.json.JSONObject
import groovy.util.logging.Log
import com.atlassian.jira.rest.client.api.domain.Issue
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import hudson.Util
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import javax.inject.Inject
import org.kohsuke.stapler.DataBoundSetter
import hudson.util.ComboBoxModel
import com.atlassian.jira.rest.client.api.domain.IssueFieldId

/**
 * @author ceilfors
 */
@ToString(includeNames = true)
@EqualsAndHashCode
@Log
abstract class IssueMatcher extends AbstractDescribableImpl<IssueMatcher> {

    final String field
    final String field_id
    final String field_name
    final String value

    protected IssueMatcher(String field, String value) {
        this.field = field
        this.value = value

        def fields = issueMatcherDescriptor.jiraClient.getFields()
        def id, name
        fields.each {
            f ->
            if (f.id == this.field || f.name == this.field) {
                id = f.id
                name = f.name
            }
        }

        this.field_id = id
        this.field_name = name
        log.finest("IssueMatcher: id is ${id}, name is ${name}, field is ${field}")
    }

    boolean matches(JSONObject issueJsonObject) {
        if (! issueJsonObject.has('fields')) {
            log.finest("yaslog: IssueMatcher.groovy:51: no fields field!");
            return false
        }
        def fields = issueJsonObject.get('fields') as JSONObject

        fields.keys().toList().find {
            def id = it
            def value = fields.get(it)

            // log.finest("checking with ${it}")
            if (fields.isNull(id)) {
                return false
            }

            if (id != this.field_id) {
                return false
            }

            if (! (value instanceof String)) {
                if (value instanceof JSONObject) {
                    value = value as JSONObject
                    if (value.has('key')) {
                        value = value.get('key').toString()
                    } else if (value.has('name')) {
                        value = value.get('name').toString()
                    } else if (value.has('value')) {
                        value = value.get('value').toString()
                    } else {
                        log.finest("Don't know how to handle value: ${value}")
                        return false
                    }
                } else {
                    log.finest("Can't work with field, don't know its value type ${value.class}: ${value}")
                    return false
                }
            } else {
                log.finest("yaslog: IssueMatcher.groovy:88: value is not JSONObject, ${value.class} ${value instanceof org.codehaus.jettison.json.JSONObject}")
            }

            if (value == this.value) {
                log.finest("matched ${this.field} to ${this.value}")
                return true;
            }

            log.finest("Don't know why not matched: ${it}, ${value}, ${value.class}, ${this}")
            return false;
        }
    }

    IssueMatcherDescriptor getIssueMatcherDescriptor() {
        super.descriptor as IssueMatcherDescriptor
    }

    static abstract class IssueMatcherDescriptor extends Descriptor<IssueMatcher> {

        @Inject
        transient JiraClient jiraClient

        @Override
        abstract String getDisplayName()

        @SuppressWarnings('GroovyUnusedDeclaration') // jelly
        ComboBoxModel doFillFieldItems() {
            def fields = jiraClient.getFields()
            new ComboBoxModel(fields.collect {it.name})
        }
    }

}
