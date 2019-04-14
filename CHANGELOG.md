# Change Log

## [Unreleased]

## [1.0.0] - 2019-04-14
### Changed
- [GH-3](https://github.com/jenkinsci/jira-trigger-plugin/pull/3) Upgraded Jenkins core version from 2.73 to 2.76

## [0.6.3] - 2018-04-24
### Fixed
- [JENKINS-43964](https://issues.jenkins-ci.org/browse/JENKINS-43964) Remove square brackets from comment replier

## [0.6.2] - 2018-04-23
### Fixed
- [JENKINS-50125](https://issues.jenkins-ci.org/browse/JENKINS-50125) Set default parameters when a job is parameterised

## [0.6.1] - 2018-04-22
### Fixed
- [JENKINS-46836](https://issues.jenkins-ci.org/browse/JENKINS-46836) Support null value parameter mapping

## [0.6.0] - 2018-04-21
### Changed (Breaking Change)
- Upgraded Java 7 to Java 8
- Upgraded Groovy from 1.8 to 2.4
- Upgraded Jenkins core version from 1.651.3 to 2.73
- Upgraded CodeNarc from 0.26.0 to 1.1
- Upgraded gradle from 2.9 to 4.6
- Upgraded gradle-jpi-plugin from 0.16.0 to 0.26.0

### Fixed
- [JENKINS-49178](https://issues.jenkins-ci.org/browse/JENKINS-49178) Comment trigger is no longer supported in JIRA Cloud
  - Add comment_created webhook event in JIRA Cloud to make this work

## [0.5.1] - 2017-09-08
### Fixed
- [JENKINS-46482](https://issues.jenkins-ci.org/browse/JENKINS-46482) Another attempt to fix this bug as the previous fix broke pipeline job

### Changed
- Update Jenkins core version from 1.651.2 to 1.651.3

## [0.5.0] - 2017-09-06
### Added
- [JENKINS-38797](https://issues.jenkins-ci.org/browse/JENKINS-38797) Support custom field parameter mapping

### Fixed
- [JENKINS-46482](https://issues.jenkins-ci.org/browse/JENKINS-46482) Parameter Mapping is not working due to SECURITY-170

### Changed
- Update Jenkins core version from 1.642 to 1.651.2

## [0.4.2] - 2017-08-11
### Fixed
- [JENKINS-43642](https://issues.jenkins-ci.org/browse/JENKINS-43642) jira-trigger-plugin used in a pipeline job is giving NullPointerException upon restart
- Restore Jenkins build by pointing to the new ci.jenkins.io

## [0.4.1] - 2017-03-15
### Fixed
- [JENKINS-41878](https://issues.jenkins-ci.org/browse/JENKINS-41878) Runtime Exception while using jira trigger plugin in maven job

## [0.4.0] - 2016-12-05
### Added
- [JENKINS-37044](https://issues.jenkins-ci.org/browse/JENKINS-37044) Support pipeline job

### Changed
- Update Jenkins core version from 1.565.3 to 1.642 

## [0.3.0] - 2016-11-27
### Fixed
- [JENKINS-39572](https://issues.jenkins-ci.org/browse/JENKINS-39572) Jenkins crash because too many files are opened
- [JENKINS-39995](https://issues.jenkins-ci.org/browse/JENKINS-39995) Password not saved in Jenkins Global Configuration

### Added
- [JENKINS-39784](https://issues.jenkins-ci.org/browse/JENKINS-39784) Utilise quiet period when scheduling build 

## [0.2.4] - 2016-10-25
### Fixed
- [JENKINS-39076](https://issues.jenkins-ci.org/browse/JENKINS-39076) Stack Overflow when trying to save system configuration in Jenkins 2.19.1

## [0.2.3] - 2016-08-01
### Added
- [JENKINS-37082](https://issues.jenkins-ci.org/browse/JENKINS-37082) Support Java 1.7
  - Previously this plugin will throw StackOverflowError when being run with Java 1.8 due to GROOVY-6818.

### Fixed
- [JENKINS-34135](https://issues.jenkins-ci.org/browse/JENKINS-34135) NPE Warning in Jenkins log while configuring a build job with JIRA Trigger

## [0.2.2] - 2016-04-19
### Fixed
- [JENKINS-34135](https://issues.jenkins-ci.org/browse/JENKINS-34135) Plugin swallows every exceptions thrown in http requests
  - Before this bug fix, when you don't have read permission for anonymous user, the user will not be redirected to the login page automatically.

## [0.2.1] - 2016-04-10
### Fixed
- [JENKINS-34135](https://issues.jenkins-ci.org/browse/JENKINS-34135) Plugin requires anonymous user to have job read permission

## [0.2.0] - 2016-02-15
### Changed (Breaking Change)
- Issue attribute path implementation is changed. It is no longer resolving values from JSON to save JIRA round trip. Read the updated help files for the new implementation.
- Jenkins webhook receiver URL is changed from /jira-trigger/ to /jira-trigger-webhook-receiver/. Update your JIRA webhook configuration.
- JIRA comment_created webhook event is no longer supported, just use issue_updated webhook event.

### Added
- Triggers a build when an issue is updated in JIRA
- Sets JIRA information as environment variables to the triggered build

## 0.1.0
### Added
- Initial release.

[Unreleased]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.6.3...HEAD
[0.6.3]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.6.2...v0.6.3
[0.6.2]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.6.1...v0.6.2
[0.6.1]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.6.0...v0.6.1
[0.6.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.5.1...v0.6.0
[0.5.1]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.4.2...v0.5.0
[0.4.2]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.4.1...v0.4.2
[0.4.1]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.4.0...v0.4.1
[0.4.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.4...v0.3.0
[0.2.4]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.3...v0.2.4
[0.2.3]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.2...v0.2.3
[0.2.2]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.1.0...v0.2.0