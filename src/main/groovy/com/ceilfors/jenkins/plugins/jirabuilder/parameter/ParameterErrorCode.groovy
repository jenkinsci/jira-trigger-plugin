package com.ceilfors.jenkins.plugins.jirabuilder.parameter

import com.ceilfors.jenkins.plugins.jirabuilder.ErrorCode

/**
 * @author ceilfors
 */
enum ParameterErrorCode implements ErrorCode {

    FAILED_TO_RESOLVE("1")

    private String code

    ParameterErrorCode(String code) {
        this.code = code
    }

    @Override
    String getCode() {
        return code
    }
}