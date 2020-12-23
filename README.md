# JIRA Trigger Plugin
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Build Status](https://ci.jenkins.io/job/Plugins/job/jira-trigger-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/jira-trigger-plugin/job/master/)
[![Java 1.8](https://img.shields.io/badge/java-1.8-red.svg)](https://java.com)

This plugin is published through the [Jenkins official plugin center](https://plugins.jenkins.io/jira-trigger).

Please consider starring the project to show your ❤️ and support.

## Features

- [x] Triggers a build when a comment is added to JIRA
- [x] Triggers a build when an issue is updated in JIRA
- [x] Transforms JIRA Webhook POST data to Jenkins parameters
- [x] Transforms JIRA standard custom fields to Jenkins parameters (See CustomFieldParameterResolverTest for the full list of supported custom field types)
- [x] Reply back to JIRA for scheduled builds
- [x] Sets JIRA information as environment variables to the triggered build 

This plugin has been tested against JIRA 7.0.0, although theoretically it should work with older version of JIRA
as long as it supports the webhook type required (see Setup section below). 

Check src/test/groovy/*AcceptanceTest to see how these features are expected to behave.

## Getting help
- Go to [Troubleshooting](#troubleshooting) section for commonly faced problems.
- Generic questions (how to, etc), ask a question at stackoverflow with [jenkins-jira-trigger tag](http://stackoverflow.com/questions/tagged/jenkins-jira-trigger).
- Ideas or bugs, file an issue to [JENKINS issue tracker](https://issues.jenkins-ci.org/secure/Dashboard.jspa) with `jira-trigger-plugin` component.

## Setup

### Add new JIRA webhook (One time) 

1. Go to JIRA > Cog > System > Advanced > WebHooks (Requires admin permission)
2. Create a new Webhook
3. Set URL to: ${Jenkins URL}/jira-trigger-webhook-receiver/ e.g. http://localhost:8080/jenkins/jira-trigger-webhook-receiver/
4. Enable _issue updated_ event
5. If you are on JIRA Cloud, enable _comment created_ event 
6. Do **not** check *Exclude body* as this plugin requires the JSON to operate
7. Save!

### Jenkins global configuration (One time)

This configuration is crucial, especially for *JQL filter* usage. 

1. Go to Jenkins global configuration (${Jenkins URL}/configure)
2. Configure *JIRA Trigger Configuration*

### Job configuration

New triggers will be made available after you have successfully install this plugin from Jenkins plugin center.
More in depth documentation about how you can configure the job are documented in the help files. Be sure to hit
those question mark buttons in Jenkins configuration page!

The configuration of Pipeline jobs are located in the job configuration page as well and 
not the Jenkinsfile, as per the screenshot shown below. The new triggers will come up next to
the "Build periodically" trigger.

If you are creating a new Pipeline job, you'll have to reconfigure the job and save it again before
the job can be triggered properly. This is currently a [known issue](https://issues.jenkins-ci.org/browse/JENKINS-42446).

#### Comment trigger
![Comment Trigger Configuration](docs/jira-comment-trigger-configuration_50.png?raw=true "Comment Trigger Configuration")

#### Changelog trigger
![Changelog Trigger Configuration](docs/jira-changelog-trigger-configuration_50.png?raw=true "Changelog Trigger Configuration")

## Environment variables

JIRA Trigger Plugin sets environment variables you can use during the build:

- `JIRA_ISSUE_KEY` - The JIRA issue key that triggers the build 

## Troubleshooting

### Build is not triggered

Firstly, enable Jenkins logging at FINE level for troubleshooting: `com.ceilfors.jenkins.plugins.jiratrigger.webhook`.
You should see "Received Webhook callback ..." log messages when Jenkins is receiving webhook events from JIRA.

If you are not seeing *anything* in the log, your problem will either be in JIRA configuration or the network connectivity
in between JIRA and Jenkins:

- Make Webhook configuration more lenient for testing:
  - Remove JQL configuration in JIRA Webhook page if you configure one
  - Try to update an issue again and check if you are getting the logs now. If not, you might have network connectivity problem, proceed below.
- If using JIRA Cloud:
  - Your Jenkins must be hosted with 80 or 443 port
- If you own JIRA Server:
  - SSH to JIRA machine.
  - Try to cURL Jenkins URL and make sure that you can get a response back.
  - If you are getting a timeout, your firewall rule might be blocking JIRA Webhook events to be sent to Jenkins. You'll need to fix this for this plugin to work.

If you are seeing "Received Webhook callback ..." but your build is not triggered, your configuration for this plugin in Jenkins might be too restrictive, please double check.

## Building Project
To build, run acceptance test, and release commands, refer to [this document](docs/Building-Project.md)

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://linkedin.com/in/rodrigc"><img src="https://avatars1.githubusercontent.com/u/1895943?v=4" width="100px;" alt=""/><br /><sub><b>Craig Rodrigues</b></sub></a><br /><a href="https://github.com/jenkinsci/jira-trigger-plugin/commits?author=rodrigc" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/sghill"><img src="https://avatars3.githubusercontent.com/u/230004?v=4" width="100px;" alt=""/><br /><sub><b>Steve Hill</b></sub></a><br /><a href="https://github.com/jenkinsci/jira-trigger-plugin/commits?author=sghill" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!