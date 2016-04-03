# JIRA Trigger Plugin

This plugin is published through Jenkins official plugin center. Plugin statistics can be found in [Jenkins wiki](http://wiki.jenkins-ci.org/display/JENKINS/JIRA+Trigger+Plugin).

## Features

- [x] Triggers a build when a comment is added to JIRA
- [x] Triggers a build when an issue is updated in JIRA
- [x] Reply back to JIRA for scheduled builds
- [x] Transforms JIRA Webhook POST data to Jenkins parameters
- [x] Sets JIRA information as environment variables to the triggered build 

This plugin has been tested against JIRA 7.0.0, although theoretically it should work with older version of JIRA
as long as it supports the webhook type required (see Setup section below). 

Check src/test/groovy/*AcceptanceTest to see how these features are expected to behave.

## Setup

### Add new JIRA webhook (One time) 

1. Go to JIRA > Cog > System > Advanced > WebHooks (Requires admin permission)
2. Create a new Webhook
3. Set URL to: ${Jenkins URL}/jira-trigger-webhook-receiver/ e.g. http://localhost:8080/jenkins/jira-trigger-webhook-receiver/
4. Set Events to: _issue updated_
5. Do **not** check *Exclude body* as this plugin requires the JSON to operate
6. Save!

To troubleshoot:

1. Enable Jenkins logging at FINE level for troubleshooting: `com.ceilfors.jenkins.plugins.jiratrigger.webhook`
2. You should see "Received Webhook callback ..." log messages when Jenkins is receiving webhook events

### Jenkins global configuration (One time)

This configuration is crucial, especially for *JQL filter* usage. 

1. Go to Jenkins global configuration (${Jenkins URL}/configure)
2. Configure *JIRA Trigger Configuration*

### Job configuration

New triggers will be made available after you have successfully install this plugin from Jenkins plugin center.
More in depth documentation about how you can configure the job are documented in the help files. Be sure to hit
those question mark buttons in Jenkins configuration page!

#### Comment trigger
![Comment Trigger Configuration](docs/jira-comment-trigger-configuration_50.png?raw=true "Comment Trigger Configuration")

#### Changelog trigger
![Changelog Trigger Configuration](docs/jira-changelog-trigger-configuration_50.png?raw=true "Changelog Trigger Configuration")

## Environment variables

JIRA Trigger Plugin sets environment variables you can use during the build:

- `JIRA_ISSUE_KEY` - The JIRA issue key that triggers the build 

## Troubleshooting

### Enable logging
Enable Jenkins logging for package: `com.ceilfors.jenkins.plugins.jiratrigger`. If nothing comes out in the log, it is possible
that your JIRA instance is unable to hit your Jenkins instance due to network connectivity issue. 

### Getting help
File a bug to [JENKINS issue tracker](https://issues.jenkins-ci.org/secure/Dashboard.jspa) with `jira-trigger-plugin` component.

## Running Acceptance Test

You will need to run JIRA locally to be able to execute the acceptance test of this plugin which is available from
vagrant. More details can be found
at [atlassian site](https://developer.atlassian.com/static/connect/docs/latest/developing/developing-locally.html).

Quick start:

1. vagrant up
2. vagrant ssh
3. Accept Oracle license term for Java
4. atlas-run-standalone --product jira --version 7.0.0 --plugins com.atlassian.jira.tests:jira-testkit-plugin:7.0.111
5. Setup JIRA project with name TEST
6. Increase the maximum number of file descriptor opened e.g. `ulimit -n 8192`. You will hit `java.net.SocketException: Bad file descriptor` otherwise.
6. ./gradlew test acceptanceTest
7. Restart JIRA if it starts to complain about license (It is using [timebomb license](https://developer.atlassian.com/market/add-on-licensing-for-developers/timebomb-licenses-for-testing) by default). 

Result of the acceptance test will be available at $buildDir/reports/acceptanceTests/index.html.
