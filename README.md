# JIRA Builder plugin
Triggers a build when a commen is added to JIRA.

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
6. ./gradlew acceptanceTest
7. Restart JIRA every 3 hours (It is using [timebomb license](https://developer.atlassian.com/market/add-on-licensing-for-developers/timebomb-licenses-for-testing) by default). 

Result of the acceptance test will be available at $buildDir/reports/acceptanceTest/index.html.