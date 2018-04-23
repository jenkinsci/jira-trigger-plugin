package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import hudson.model.Job
import hudson.model.ParameterValue
import hudson.model.ParametersAction
import hudson.model.ParametersDefinitionProperty

/**
 * <tt>DefaultParametersAction</tt> is required because we are using <tt>ParametersAction</tt>
 * in <tt>ParametersMappingAction</tt>. The default values of a parameterised job is only
 * automatically populated by ParameterizedJobMixIn.scheduleBuild2 when <tt>ParametersAction</tt>
 * absent from the scheduled actions. The proper solution is to use <tt>EnvironmentContributingAction</tt>
 * instead of <tt>ParametersMappingAction</tt>, but unfortunately this solution is not viable due to JENKINS-46482.
 *
 * Generating default values by ourselves (instead of relying on #scheduleBuild2) seems to be a common solution in
 * the community. Example: https://github.com/jenkinsci/ghprb-plugin/blob/c15d5106e78f7f028c6305dccf01b77cc9a724b3/
 * src/main/java/org/jenkinsci/plugins/ghprb/GhprbTrigger.java#L386
 *
 * @author ceilfors
 */
class DefaultParametersAction extends ParametersAction {

    DefaultParametersAction(Job job) {
        super(getDefaultParameters(job))
    }

    private static List<ParameterValue> getDefaultParameters(Job job) {
        ParametersDefinitionProperty pdp = job.getProperty(ParametersDefinitionProperty)
        pdp != null ? pdp.parameterDefinitions*.defaultParameterValue : []
    }
}
