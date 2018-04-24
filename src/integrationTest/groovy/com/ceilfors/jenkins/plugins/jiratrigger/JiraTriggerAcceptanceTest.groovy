package com.ceilfors.jenkins.plugins.jiratrigger

import com.ceilfors.jenkins.plugins.jiratrigger.integration.FakeJiraCloudRunner
import com.ceilfors.jenkins.plugins.jiratrigger.integration.FakeJiraRunner
import com.ceilfors.jenkins.plugins.jiratrigger.integration.JenkinsRunner
import com.ceilfors.jenkins.plugins.jiratrigger.integration.JiraRunner
import com.ceilfors.jenkins.plugins.jiratrigger.integration.JulLogLevelRule
import com.ceilfors.jenkins.plugins.jiratrigger.jira.JiraClient
import org.junit.Rule
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import spock.lang.Specification

import static com.ceilfors.jenkins.plugins.jiratrigger.JiraCommentTrigger.DEFAULT_COMMENT
import static com.ceilfors.jenkins.plugins.jiratrigger.integration.FakeJiraRunner.CUSTOM_FIELD_NAME

/**
 * @author ceilfors
 */
class JiraTriggerAcceptanceTest extends Specification {

    JenkinsRunner jenkins = new JenkinsRunner()
    JiraRunner jira
    JiraRunner jiraCloud

    @Rule
    RuleChain ruleChain = RuleChain
            .outerRule(new JulLogLevelRule())
            .around(jenkins)
            .around(
            new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    jira = new FakeJiraRunner(jenkins)
                    jiraCloud = new FakeJiraCloudRunner(jenkins)
                }
            })

    @Rule
    TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.err.println('-------------------------------------------------------')
            System.err.println("Starting test: ${description.methodName}")
            System.err.println('-------------------------------------------------------')
        }
    }

    def 'Should trigger a build when a comment is added'() {
        given:
        String issueKey = jira.createIssue()
        jenkins.createJiraCommentTriggeredProject('job')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    def 'Should trigger a build when a comment is added in JIRA cloud'() {
        given:
        String issueKey = jiraCloud.createIssue()
        jenkins.createJiraCommentTriggeredProject('job')

        when:
        jiraCloud.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    def 'Should trigger a build when an issue is updated'() {
        given:
        String issueKey = jira.createIssue('Original description')
        jenkins.createJiraChangelogTriggeredProject('job')

        when:
        jira.updateDescription(issueKey, 'New description')

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    def "Should reply back to JIRA when a build is scheduled"() {
        given:
        def expectedComment
        jenkins.quietPeriod = 100
        String issueKey = jira.createIssue()
        def project = jenkins.createJiraCommentTriggeredProject('job')

        def jiraClient = Mock(JiraClient)
        jenkins.setJiraClient(jiraClient)

        when:
        jenkins.setJiraCommentReply(true)
        expectedComment = "Build is scheduled for: ${project.absoluteUrl}"
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        1 * jiraClient.addComment(issueKey, { it ==~ expectedComment })

        when:
        def project2 = jenkins.createJiraCommentTriggeredProject('job2')
        expectedComment = "Build is scheduled for: ${project.absoluteUrl}, ${project2.absoluteUrl}"
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        1 * jiraClient.addComment(issueKey, { it ==~ expectedComment })
    }

    def 'Should map parameters to the triggered build when a comment is created'() {
        given:
        String issueKey = jira.createIssue('Dummy issue description')
        def project = jenkins.createJiraCommentTriggeredProject('simpleJob')
        project.addParameterMapping('jenkins_description', 'description')
        project.addParameterMapping('jenkins_key', 'key')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled('simpleJob', [
                'jenkins_description': 'Dummy issue description',
                'jenkins_key'        : issueKey,
        ])
    }

    def 'Should map parameters to the triggered build when an issue is updated'() {
        given:
        String issueKey = jira.createIssue('Dummy issue description')
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.addParameterMapping('jenkins_description', 'description')
        project.addParameterMapping('jenkins_key', 'key')

        when:
        jira.updateDescription(issueKey, 'New description')

        then:
        jenkins.buildShouldBeScheduled('job', [
                'jenkins_description': 'New description',
                'jenkins_key'        : issueKey,
        ])
    }

    def 'Should be able to map custom field when custom field parameter mapping is used'() {
        given:
        String issueKey = jira.createIssue('Dummy issue description')
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.addCustomFieldParameterMapping('JIRA_CUSTOMER', '10000')

        when:
        jira.updateCustomField(issueKey, CUSTOM_FIELD_NAME, 'Barclays')

        then:
        jenkins.buildShouldBeScheduled('job', [
                'JIRA_CUSTOMER': 'Barclays',
        ])
    }

    def 'Should trigger a build when a comment matches the comment pattern'() {
        given:
        String issueKey = jira.createIssue()
        def project = jenkins.createJiraCommentTriggeredProject('job')
        project.setCommentPattern('.*jira trigger.*')

        when:
        jira.addComment(issueKey, 'bla jira trigger bla')

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    def 'Should not trigger a build when a comment does not match the comment pattern'() {
        given:
        String issueKey = jira.createIssue()
        def project = jenkins.createJiraCommentTriggeredProject('job')
        project.setCommentPattern('.*jira trigger.*')

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Should trigger a build when an issue is updated and the issue key matches the JQL filter'() {
        given:
        String issueKey = jira.createIssue('dummy description')
        String jqlFilter = 'type=task and description~"New description" and status="To Do"'
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.setJqlFilter(jqlFilter)

        def jiraClient = Mock(JiraClient)
        jenkins.setJiraClient(jiraClient)

        when:
        jira.updateDescription(issueKey, 'New description')

        then:
        jenkins.buildShouldBeScheduled('job')
        1 * jiraClient.validateIssueKey(issueKey, jqlFilter) >> true
    }

    def 'Should not trigger a build when an issue is updated but the issue does not match the JQL filter'() {
        given:
        String issueKey = jira.createIssue('dummy description')
        String jqlFilter = 'type=task and description~"New description" and status="Done"'
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.setJqlFilter(jqlFilter)

        def jiraClient = Mock(JiraClient)
        jenkins.setJiraClient(jiraClient)

        when:
        jira.updateDescription(issueKey, 'New description')

        then:
        jenkins.noBuildShouldBeScheduled()
        1 * jiraClient.validateIssueKey(issueKey, jqlFilter) >> false
    }

    def 'Should trigger a build when a comment is added and the issue matches the JQL filter'() {
        given:
        String issueKey = jira.createIssue('dummy description')
        String jqlFilter = 'type=task and description~"dummy description" and status="To Do"'
        def project = jenkins.createJiraCommentTriggeredProject('job')
        project.setJqlFilter(jqlFilter)

        def jiraClient = Mock(JiraClient)
        jenkins.setJiraClient(jiraClient)

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.buildShouldBeScheduled('job')
        1 * jiraClient.validateIssueKey(issueKey, jqlFilter) >> true
    }

    def 'Should not trigger a build when a comment is added but the issue does not match JQL filter'() {
        given:
        String issueKey = jira.createIssue('dummy description')
        def project = jenkins.createJiraCommentTriggeredProject('job')
        String jqlFilter = 'type=task and status="Done"'
        project.setJqlFilter(jqlFilter)

        def jiraClient = Mock(JiraClient)
        jenkins.setJiraClient(jiraClient)

        when:
        jira.addComment(issueKey, DEFAULT_COMMENT)

        then:
        jenkins.noBuildShouldBeScheduled()
        1 * jiraClient.validateIssueKey(issueKey, jqlFilter) >> false
    }

    def 'Should trigger a build when issue status is updated to Done'() {
        given:
        String issueKey = jira.createIssue('original description')
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.addJiraFieldChangelogMatcher('status', '', 'Done')

        when:
        jira.updateStatus(issueKey, 'Done')

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    def 'Should trigger a build when issue status is updated from To Do to Done'() {
        given:
        String issueKey = jira.createIssue('original description')
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.addJiraFieldChangelogMatcher('status', 'To Do', 'Done')

        when:
        jira.updateStatus(issueKey, 'Done')

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    def 'Should not trigger a build when issue status is updated to In Progress whilst Done is expected'() {
        given:
        String issueKey = jira.createIssue('original description')
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.addJiraFieldChangelogMatcher('status', '', 'Done')

        when:
        jira.updateStatus(issueKey, 'In Progress')

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Should not trigger a build when old status value does not match the matcher'() {
        given:
        String issueKey = jira.createIssue('original description')
        jira.updateStatus(issueKey, 'In Progress')
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.addJiraFieldChangelogMatcher('status', 'To Do', 'Done')

        when:
        jira.updateStatus(issueKey, 'Done')

        then:
        jenkins.noBuildShouldBeScheduled()
    }

    def 'Should trigger a build when a custom field is updated'() {
        given:
        String issueKey = jira.createIssue('original description')
        def project = jenkins.createJiraChangelogTriggeredProject('job')
        project.addCustomFieldChangelogMatcher(CUSTOM_FIELD_NAME, '', 'Barclays')

        when:
        jira.updateCustomField(issueKey, CUSTOM_FIELD_NAME, 'Barclays')

        then:
        jenkins.buildShouldBeScheduled('job')
    }

    // Acceptance Test - Check CauseAction in JenkinsRunner to differentiate trigger? Can be retrieved at
    //   Queue.Item.getActions()
    // Improvement - void method in JrjcJiraClient should be async. Be careful on concurrency issues in this test case.
    // How to enable JenkinsRule as ClassRule to make the build faster?
    // Trigger - JiraTriggerCause should contain issue key and link
    // Acceptance Test - Override UncaughtExceptionHandler in Acceptance Test to catch Exception, especially when
    //   webhook is configured wrongly and Acceptance test don't see any error
    // Improvement - Check SequentialExecutionQueue that is used by GitHubWebHook
    // Improvement - Should JiraWebhook be RootAction rather than UnprotectedRootAction? Check out
    //   RequirePostWithGHHookPayload
    // Improvement - Translate JiraTriggerException to error messages
}
