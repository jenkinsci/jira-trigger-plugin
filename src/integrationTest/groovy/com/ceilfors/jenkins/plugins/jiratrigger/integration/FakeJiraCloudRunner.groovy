package com.ceilfors.jenkins.plugins.jiratrigger.integration

/**
 * @author ceilfors
 */
class FakeJiraCloudRunner extends FakeJiraRunner {

    FakeJiraCloudRunner(JenkinsRunner jenkinsRunner) {
        super(jenkinsRunner)
    }

    @Override
    void addComment(String issueKey, String comment) {
        Map body = createPostBody('cloudAddComment', issueKey)
        body.comment.body = comment
        restClient.post(body: body)
    }
}
