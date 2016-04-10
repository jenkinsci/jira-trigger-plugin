# Building Locally
- Running test and integration test: `./gradlew clean test`
- Running Jenkins with the plugin pre-installed: `./gradlew server`

See Jenkins [Gradle JPI Plugin page](https://wiki.jenkins-ci.org/display/JENKINS/Gradle+JPI+Plugin) for more details.

# Running Acceptance Test

You will need to run JIRA locally to be able to execute the acceptance test of this plugin which is available from
vagrant. More details can be found
at [atlassian site](https://developer.atlassian.com/static/connect/docs/latest/developing/developing-locally.html).

Quick start:

1. `vagrant up`
2. `vagrant ssh`
3. Accept Oracle license term for Java
4. `atlas-run-standalone --product jira --version 7.0.0 --plugins com.atlassian.jira.tests:jira-testkit-plugin:7.0.111`
5. Setup JIRA project with name TEST
6. Increase the maximum number of file descriptor opened e.g. `ulimit -n 8192`. You will hit `java.net.SocketException: Bad file descriptor` otherwise.
6. `./gradlew test acceptanceTest`
7. Restart JIRA if it starts to complain about license (It is using [timebomb license](https://developer.atlassian.com/market/add-on-licensing-for-developers/timebomb-licenses-for-testing) by default). 

Result of the acceptance test will be available at `$buildDir/reports/acceptanceTests/index.html`.

# Release

1. Run acceptance test! It's not integrated in CI yet.
2. `git tag vx.x.x`
3. `./gradlew clean publish`
4. `git push --tags`
5. Update github release page.
