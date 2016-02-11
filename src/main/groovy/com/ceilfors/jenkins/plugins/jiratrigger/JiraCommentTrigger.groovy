package com.ceilfors.jenkins.plugins.jiratrigger

import com.atlassian.jira.rest.client.api.domain.Comment
import com.atlassian.jira.rest.client.api.domain.Issue
import groovy.util.logging.Log
import hudson.Extension
import hudson.model.Cause
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

/**
 * Responsible for processing <tt>Comment</tt> and determine if a job should be scheduled.
 *
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
