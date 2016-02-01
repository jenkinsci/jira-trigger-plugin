package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.parameter.IssueAttributePathParameterMapping
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.Cause
import hudson.model.ParameterValue
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

import java.util.logging.Level

/**
 * @author ceilfors
 */
@Log
class JiraCommentTrigger extends JiraTrigger<Comment> {

    public static final String DEFAULT_COMMENT = "build this please"
    private String commentPattern = JiraCommentTriggerDescriptor.DEFAULT_COMMENT_PATTERN

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

    @Override
    boolean filter(Issue issue, Comment comment) {
        def commentBody = comment.body
        if (commentPattern) {
            if (!(commentBody ==~ commentPattern)) {
                log.fine("[${job.fullName}] - Not scheduling build: commentPattern [$commentPattern] doesn't match with the comment body [$commentBody]")
                return false
            }
        }
        return true
    }

   protected List<ParameterValue> collectParameterValues(Issue issue, Comment comment) {
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

    @Override
    Cause getCause(Issue issue, Comment comment) {
        return new JiraCommentTriggerCause()
    }

    @SuppressWarnings("UnnecessaryQualifiedReference")
    @Extension
    static class JiraCommentTriggerDescriptor extends JiraTrigger.JiraTriggerDescriptor {

        @SuppressWarnings("GroovyUnusedDeclaration") // Jenkins jelly
        public static final String DEFAULT_COMMENT_PATTERN = "(?i)${DEFAULT_COMMENT}"

        public String getDisplayName() {
            return "Build when a comment is added to JIRA"
        }
    }

    static class JiraCommentTriggerCause extends Cause {

        @Override
        String getShortDescription() {
            return "JIRA comment is added"
        }
    }
}
