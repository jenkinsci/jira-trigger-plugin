package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterMapping
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.ParameterResolver
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.*
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import jenkins.model.Jenkins
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import javax.inject.Inject
import java.util.logging.Level
/**
 * @author ceilfors
 */
@Log
class JiraCommentTrigger extends Trigger<BuildableItem> {

    public static final String DEFAULT_COMMENT = "build this please"
    private String commentPattern = DescriptorImpl.DEFAULT_COMMENT_PATTERN
    private String jqlFilter = ""
    private List<ParameterMapping> parameterMappings = []
    private int quietPeriod

    @DataBoundConstructor
    JiraCommentTrigger() {
    }

    String getCommentPattern() {
        return commentPattern
    }

    @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins DataBoundSetter
    @DataBoundSetter
    void setCommentPattern(String commentPattern) {
        this.commentPattern = commentPattern
    }

    List<ParameterMapping> getParameterMappings() {
        return Collections.unmodifiableList(parameterMappings)
    }

    @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins DataBoundSetter
    @DataBoundSetter
    void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings
    }

    String getJqlFilter() {
        return jqlFilter
    }

    @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins DataBoundSetter
    @DataBoundSetter
    void setJqlFilter(String jqlFilter) {
        this.jqlFilter = jqlFilter
    }

    void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod
    }

    @Override
    DescriptorImpl getDescriptor() {
        return super.getDescriptor() as DescriptorImpl
    }

    boolean run(Issue issue, Comment comment) {
        log.fine("[${job.fullName}] - Processing comment ${comment.self}")
        def commentBody = comment.body
        if (commentPattern) {
            if (!(commentBody ==~ commentPattern)) {
                log.fine("[${job.fullName}] - Not scheduling build: commentPattern [$commentPattern] doesn't match with the comment body [$commentBody]")
                return false
            }
        }
        if (jqlFilter) {
            if (!descriptor.jiraClient.validateIssueKey(issue.key, jqlFilter)) {
                log.fine("[${job.fullName}] - Not scheduling build: The issue ${issue.key} doesn't match with the jqlFilter [$jqlFilter]")
                return false
            }
        }

        List<Action> actions = []
        if (parameterMappings) {
            actions << new ParametersAction(collectParameterValues(issue, comment))
        }
        log.fine("[${job.fullName}] - Scheduling build for ${comment.self}")
        return job.scheduleBuild(quietPeriod, new JiraCommentTriggerCause(), *actions)
    }

    private List<ParameterValue> collectParameterValues(Issue issue, Comment comment) {
        return parameterMappings.collect {
            if (it instanceof IssueAttributePathParameterMapping) {
                try {
                    return descriptor.parameterResolver.resolve(issue, comment, it)
                } catch (JiraTriggerException e) {
                    log.log(Level.WARNING, "Can't resolve attribute ${it.issueAttributePath} from JIRA issue. Example: fields.description, key, fields.project.key", e)
                    return null
                }
            } else {
                throw new UnsupportedOperationException("Unsupported parameter mapping ${it.class}")
            }
        } - null
    }

    @Extension
    static class DescriptorImpl extends TriggerDescriptor {

        @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins jelly
        public static final String DEFAULT_COMMENT_PATTERN = "(?i)${DEFAULT_COMMENT}"

        @Inject
        private Jenkins jenkins

        @Inject
        private JiraClient jiraClient

        @Inject
        private ParameterResolver parameterResolver

        public boolean isApplicable(Item item) {
            return item instanceof BuildableItem
        }

        public String getDisplayName() {
            return "Build when a comment is added to JIRA"
        }

        @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins jelly
        public List<ParameterMapping.ParameterMappingDescriptor> getParameterMappingDescriptors() {
            return jenkins.getDescriptorList(ParameterMapping)
        }
    }

    static class JiraCommentTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            return "JIRA comment is added"
        }
    }
}
