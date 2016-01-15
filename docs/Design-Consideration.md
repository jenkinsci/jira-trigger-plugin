# Why not JIRA plugin?

Jenkins plugin has been chosen as it is perceived that more projects will use JIRA in the cloud and
hosting their own Jenkins server. It is a lot easier for a Jenkins plugin to just hit a JIRA Cloud service
than making a JIRA plugin to communicate with your own hosted Jenkins server e.g. exposing it to the internet.

This plugin will try to have the communication protocol to work only in one direction, which is only from
 Jenkins to JIRA. With that said, in its initial version, JIRA Trigger will only support JIRA webhooks as
 it is a lot easier to develop and add the support of polling after that.
 
# Avoiding library with net.sf.json-lib:json-lib transitive dependency

Using a library that has a transitive dependency to `net.sf.json-lib:json-lib` will introduce an error when being
used in conjunction with `GlobalConfiguration` Jenkins extension. This is happening because Jenkins already has a
dependency to `org.kohsuke.stapler:json-lib`, which is a patched version of `net.sf.json-lib:json-lib`:
 
```
java.lang.LinkageError: loader constraint violation: loader (instance of hudson/PluginFirstClassLoader) previously initiated loading for a different type with name "net/sf/json/JSONObject"
...
	at hudson.ExtensionFinder$GuiceFinder$SezpozModule.resolve(ExtensionFinder.java:484)
	at com.google.inject.AbstractModule.configure(AbstractModule.java:62)
...
	at com.google.inject.Guice.createInjector(Guice.java:73)
	at hudson.ExtensionFinder$GuiceFinder.<init>(ExtensionFinder.java:282)
...
```

The patched version of `org.kohsuke.stapler:json-lib` itself is problematic. At of the 
time of writing, the method
`net.sf.json.AbstractJSON#_processValue` behaves differently even though the version is the same:
org.kohsuke.stapler:json-lib:2.4-jenkins-3 vs net.sf.json-lib:json-lib:2.4. There is [a commit that
fixes this issue since 2009](https://github.com/jenkinsci/json-lib/commit/3115c86237981793e162a1d95917bf2d686a1705)
that somehow is not released until now.

One of the library that has been used and problematic in the past is `net.rcarz:jira-client:0.5`. Excluding its
transitive dependency to `json-lib` will introduce a problem during JIRA issue creation 
because the POST request body is not constructed properly by `AbstractJSON#_processValue`.
 
The value which failed to be processed is in this form: `{"key":"JIRA_PROJECT_TEST"}`. This value is supposed
to be resolved by the `JSONUtils.mayBeJSON` condition in `AbstractJSON`.

