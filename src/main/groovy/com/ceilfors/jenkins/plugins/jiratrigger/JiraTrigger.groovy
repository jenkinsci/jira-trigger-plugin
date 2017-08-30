package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.AddressableEntity
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterMapping
import groovy.util.logging.Log
import hudson.model.Action
import hudson.model.Cause
import hudson.model.CauseAction
import hudson.model.Item
import hudson.model.Job
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import jenkins.model.Jenkins
import jenkins.model.ParameterizedJobMixIn
import org.kohsuke.stapler.DataBoundSetter

import javax.inject.Inject
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author ceilfors
 */
@SuppressWarnings('Instanceof')
@Log
abstract class JiraTrigger<T> extends Trigger<Job> {

    @DataBoundSetter
    String jqlFilter = ''

    @DataBoundSetter
    List<ParameterMapping> parameterMappings = []

    final boolean run(Issue issue, T t) {
        log.fine("[${job.fullName}] - Processing ${issue.key} - ${getId(t)}")

        if (!filter(issue, t)) {
            return false
        }
        if (jqlFilter) {
            if (!jiraTriggerDescriptor.jiraClient.validateIssueKey(issue.key, jqlFilter)) {
                log.fine("[${job.fullName}] - Not scheduling build: The issue ${issue.key} doesn't " +
                        "match with the jqlFilter [$jqlFilter]")
                return false
            }
        }

        List<Action> actions = []
        if (parameterMappings) {
            actions << new ParameterMappingAction(issue, parameterMappings)
        }
        actions << new JiraIssueEnvironmentContributingAction(issue)
        actions << new CauseAction(getCause(issue, t))
        log.fine("[${job.fullName}] - Scheduling build for ${issue.key} - ${getId(t)}")

        ParameterizedJobMixIn.scheduleBuild2(job, -1, *actions) != null
    }

    @Override
    void start(Job project, boolean newInstance) {
        super.start(project, newInstance)
        jiraTriggerDescriptor.addTrigger(this)
    }

    @Override
    void stop() {
        super.stop()
        jiraTriggerDescriptor.removeTrigger(this)
    }

    Job getJob() {
        super.job
    }

    abstract boolean filter(Issue issue, T t)

    private String getId(T t) {
        t instanceof AddressableEntity ? (t as AddressableEntity).self : t.toString()
    }

    JiraTriggerDescriptor getJiraTriggerDescriptor() {
        super.descriptor as JiraTriggerDescriptor
    }

    abstract Cause getCause(Issue issue, T t)

    @SuppressWarnings('UnnecessaryTransientModifier')
    @Log
    static abstract class JiraTriggerDescriptor extends TriggerDescriptor {

        @Inject
        protected transient Jenkins jenkins

        @Inject
        transient JiraClient jiraClient

        private transient final List<JiraTrigger> triggers = new CopyOnWriteArrayList<>()

        @Override
        boolean isApplicable(Item item) {
            item instanceof Job && item instanceof ParameterizedJobMixIn.ParameterizedJob
        }

        @SuppressWarnings('GroovyUnusedDeclaration') // Jenkins jelly
        List<ParameterMapping.ParameterMappingDescriptor> getParameterMappingDescriptors() {
            jenkins.getDescriptorList(ParameterMapping)
        }

        protected void addTrigger(JiraTrigger jiraTrigger) {
            triggers.add(jiraTrigger)
            log.finest("Added [${jiraTrigger.job.fullName}]:[${jiraTrigger.class.simpleName}] to triggers list")
        }

        protected void removeTrigger(JiraTrigger jiraTrigger) {
            boolean result = triggers.remove(jiraTrigger)
            if (result) {
                log.finest("Removed [${jiraTrigger.job.fullName}]:[${jiraTrigger.class.simpleName}] from triggers list")
            } else {
                if (jiraTrigger.job) {
                    log.warning(
                            "Bug! Failed to remove [${jiraTrigger.job.fullName}]:[${jiraTrigger.class.simpleName}] " +
                                    'from triggers list. ' +
                                    'The job might accidentally be triggered by JIRA. Restart Jenkins to recover.')
                } else {
                    log.finest('Failed to remove trigger as it might not be started yet.' +
                            'This is normal for pipeline job.')
                }
            }
        }

        List<JiraTrigger> allTriggers() {
            Collections.unmodifiableList(triggers)
        }

        @Override
        abstract String getDisplayName()
    }
}
