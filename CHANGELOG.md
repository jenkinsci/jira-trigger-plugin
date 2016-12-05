# Change Log

## [Unreleased]

## [0.4.0] - 2016-12-05
### Added
- [JENKINS-37044](https://issues.jenkins-ci.org/browse/JENKINS-37044) Support pipeline job

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

[Unreleased]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.4.0...HEAD
[0.4.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.4...v0.3.0
[0.2.4]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.3...v0.2.4
[0.2.3]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.2...v0.2.3
[0.2.2]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.1...v0.2.2
[0.2.1]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.2.0...v0.2.1
[0.2.0]: https://github.com/jenkinsci/jira-trigger-plugin/compare/v0.1.0...v0.2.0