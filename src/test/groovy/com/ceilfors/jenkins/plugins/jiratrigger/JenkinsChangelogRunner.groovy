package com.ceilfors.jenkins.plugins.jiratrigger
import com.gargoylesoftware.htmlunit.html.HtmlPage
import hudson.model.AbstractProject
import jenkins.model.Jenkins
/**
 * @author ceilfors
 */
class JenkinsChangelogRunner {

    private JenkinsRunner jenkinsRunner
    private Jenkins instance
    private String jobName

    public JenkinsChangelogRunner(JenkinsRunner jenkinsRunner, String jobName) {
        this.jenkinsRunner = jenkinsRunner
        this.instance = jenkinsRunner.instance
        this.jobName = jobName
    }

    JiraChangelogTriggerConfigurationPage configure() {
        HtmlPage htmlPage = jenkinsRunner.createWebClient().goTo("job/$jobName/configure")
        return new JiraChangelogTriggerConfigurationPage(htmlPage)
    }

    private JiraChangelogTrigger getTrigger() {
        instance.getItemByFullName(jobName, AbstractProject).getTrigger(JiraChangelogTrigger)
    }
}
