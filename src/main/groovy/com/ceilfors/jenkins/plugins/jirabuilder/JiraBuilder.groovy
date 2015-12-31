package com.ceilfors.jenkins.plugins.jirabuilder
import com.ceilfors.jenkins.plugins.jirabuilder.jira.JiraClient
import com.ceilfors.jenkins.plugins.jirabuilder.parameter.IssueAttributePathParameterMapping
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhookContext
import com.ceilfors.jenkins.plugins.jirabuilder.webhook.JiraWebhookListener
import com.google.inject.Singleton
import groovy.util.logging.Log
import hudson.model.*
import jenkins.model.Jenkins

import javax.inject.Inject
/**
 * @author ceilfors
 */
@Singleton
@Log
class JiraBuilder implements JiraWebhookListener {

    private Jenkins jenkins
    private JiraClient jira
    private List<JiraBuilderListener> jiraBuilderListeners = []
    private int quietPeriod = 0

    @Inject
    public JiraBuilder(Jenkins jenkins, JiraClient jira) {
        this.jenkins = jenkins
        this.jira = jira
    }

    void addJiraBuilderListener(JiraBuilderListener jiraBuilderListener) {
        jiraBuilderListeners << jiraBuilderListener
    }

    void setQuietPeriod(int quietPeriod) {
        this.quietPeriod = quietPeriod
    }

    @Override
    void commentCreated(JiraWebhookContext jiraWebhookContext) {
        def jobs = jenkins.getAllItems(AbstractProject).findAll { it.getTrigger(JiraBuilderTrigger) }
        if (jobs) {
            log.fine("Found jobs: ${jobs.collect { it.name }}")
            boolean buildScheduled = false
            def commentBody = jiraWebhookContext.eventBody.body as String
            for (job in jobs) {
                JiraBuilderTrigger trigger = job.getTrigger(JiraBuilderTrigger)
                if (trigger.commentPattern) {
                    if (!(commentBody ==~ trigger.commentPattern)) {
                        log.fine("[${job.fullName}] commentPattern doesn't match with the comment body, not scheduling build")
                        break
                    }
                }
                if (trigger.jqlFilter) {
                    if (!jira.validateIssueKey(jiraWebhookContext.issueKey, trigger.jqlFilter)) {
                        log.fine("[${job.fullName}] jqlFilter doesn't match with the JQL filter, not scheduling build")
                        break
                    }
                }

                List<Action> actions = []
                if (trigger.parameterMappings) {
                    def issue = jira.getIssueMap(jiraWebhookContext.issueKey)
                    actions << new ParametersAction(collectParameterValues(trigger, issue))
                }
                job.scheduleBuild2(quietPeriod, new JiraBuilderTrigger.JiraBuilderTriggerCause(), actions)
                jiraBuilderListeners*.buildScheduled(jiraWebhookContext.issueKey, commentBody, job.fullName)
                buildScheduled = true
            }
            if (!buildScheduled) {
                jiraBuilderListeners*.buildNotScheduled(jiraWebhookContext.issueKey, commentBody)
            }
        } else {
            log.fine("Couldn't find any jobs that have JiraBuildTrigger configured")
        }
    }

    private List<ParameterValue> collectParameterValues(JiraBuilderTrigger jiraBuilderTrigger, Map issue) {
        jiraBuilderTrigger.parameterMappings.collect {
            if (it instanceof IssueAttributePathParameterMapping) {
                IssueAttributePathParameterMapping parameterMapping = it

                def attributeValue = GroovyUtils.resolveProperty(issue, parameterMapping.issueAttributePath)
                if (attributeValue) {
                    return new StringParameterValue(parameterMapping.jenkinsParameter, attributeValue as String)
                } else {
                    log.warning("Can't resolve attribute ${parameterMapping.issueAttributePath} from JIRA issue. Example: fields.description, key, fields.project.key")
                    return null
                }
            } else {
                throw new UnsupportedOperationException("Unsupported parameter mapping ${it.class}")
            }
        } - null
    }
}
