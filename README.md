# JIRA Trigger plugin
Triggers a build when a comment is added to JIRA.

![Jira Trigger Configuration](docs/jira-trigger-configuration.png?raw=true "Jira Trigger Configuration")

## Setting up JIRA Webhook

1. Go to JIRA > Cog > System > Advanced > WebHooks
2. Create a new Webhook
3. Set URL to: ${Jenkins URL}/jira-trigger/ e.g. http://localhost:8080/jenkins/jira-trigger/
4. Set Events to: _comment created_ or _issue updated_
5. Enable Jenkins logging at FINE level for troubleshooting: com.ceilfors.jenkins.plugins.jiratrigger.webhook
6. You should see "Received Webhook callback ..." log messages when Jenkins is receiving webhook events

## Running Acceptance Test

You will need to run JIRA locally to be able to execute the acceptance test of this plugin which is available from
vagrant. More details can be found
at [atlassian site](https://developer.atlassian.com/static/connect/docs/latest/developing/developing-locally.html).

Quick start:

1. vagrant up
2. vagrant ssh
3. Accept Oracle license term for Java
4. atlas-run-standalone --product jira --version 7.0.0
5. Setup JIRA project with name TEST
6. ./gradlew test acceptanceTest
7. Restart JIRA every 3 hours (It is using [timebomb license](https://developer.atlassian.com/market/add-on-licensing-for-developers/timebomb-licenses-for-testing) by default). 

Result of the acceptance test will be available at $buildDir/reports/acceptanceTest/index.html.