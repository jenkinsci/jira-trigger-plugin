package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.AddressableEntity
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterMapping
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterResolver
import groovy.util.logging.Log
import hudson.model.*
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import jenkins.model.Jenkins
import org.kohsuke.stapler.DataBoundSetter

import javax.inject.Inject
import java.util.logging.Level
/**
 * @author ceilfors
 */
@Log
abstract class JiraTrigger<T> extends Trigger<AbstractProject> {

    private int quietPeriod
    private String jqlFilter = ""
    private List<ParameterMapping> parameterMappings = []

    int getQuietPeriod() {
        return quietPeriod
    }

    void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod
    }

    String getJqlFilter() {
        return jqlFilter
    }

    @DataBoundSetter
    void setJqlFilter(String jqlFilter) {
        this.jqlFilter = jqlFilter
    }

    List<ParameterMapping> getParameterMappings() {
        return Collections.unmodifiableList(parameterMappings)
    }

    @DataBoundSetter
    void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings
    }

    final boolean run(Issue issue, T t) {
        log.fine("[${job.fullName}] - Processing ${issue.key} - ${getId(t)}")

        if (!filter(issue, t)) {
            return false
        }
        if (jqlFilter) {
            if (!descriptor.jiraClient.validateIssueKey(issue.key, jqlFilter)) {
                log.fine("[${job.fullName}] - Not scheduling build: The issue ${issue.key} doesn't match with the jqlFilter [$jqlFilter]")
                return false
            }
        }

        List<Action> actions = []
        if (parameterMappings) {
            actions << new ParametersAction(collectParameterValues(issue))
        }
        actions << new JiraIssueEnvironmentContributingAction(issue: issue)
        log.fine("[${job.fullName}] - Scheduling build for ${issue.key} - ${getId(t)}")
        return job.scheduleBuild(quietPeriod, getCause(issue, t), *actions)
    }

    abstract boolean filter(Issue issue, T t)

    protected List<ParameterValue> collectParameterValues(Issue issue) {
        return parameterMappings.collect {
            if (it instanceof IssueAttributePathParameterMapping) {
                try {
                    return descriptor.parameterResolver.resolve(issue, it)
                } catch (JiraTriggerException e) {
                    log.log(Level.WARNING, "Can't resolve attribute ${it.issueAttributePath} from JIRA issue. Example: fields.description, key, fields.project.key", e)
                    return null
                }
            } else {
                throw new UnsupportedOperationException("Unsupported parameter mapping ${it.class}")
            }
        } - null
    }

    private String getId(T t) {
        if (t instanceof AddressableEntity) {
            return (t as AddressableEntity).self
        } else {
            return t.toString()
        }
    }

    @Override
    JiraTriggerDescriptor getDescriptor() {
        return super.getDescriptor() as JiraTriggerDescriptor
    }

    abstract Cause getCause(Issue issue, T t)

    static abstract class JiraTriggerDescriptor extends TriggerDescriptor {

        @Inject
        protected Jenkins jenkins

        @Inject
        protected JiraClient jiraClient

        @Inject
        protected ParameterResolver parameterResolver

        public boolean isApplicable(Item item) {
            return item instanceof AbstractProject
        }

        @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins jelly
        public List<ParameterMapping.ParameterMappingDescriptor> getParameterMappingDescriptors() {
            return jenkins.getDescriptorList(ParameterMapping)
        }
    }
}
