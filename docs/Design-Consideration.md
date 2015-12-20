# Why not JIRA plugin?

Jenkins plugin has been chosen as it is perceived that more projects will use JIRA in the cloud and
hosting their own Jenkins server. It is a lot easier for a Jenkins plugin to just hit a JIRA Cloud service
than making a JIRA plugin to communicate with your own hosted Jenkins server e.g. exposing it to the internet.

This plugin will try to have the communication protocol to work only in one direction, which is only from
 Jenkins to JIRA. With that said, in its initial version, JIRA Builder will only support JIRA webhooks as
 it is a lot easier to develop and add the support of polling after that.