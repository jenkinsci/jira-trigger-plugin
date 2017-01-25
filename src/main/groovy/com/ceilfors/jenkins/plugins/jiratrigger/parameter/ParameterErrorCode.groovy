package com.ceilfors.jenkins.plugins.jiratrigger.parameter

import com.ceilfors.jenkins.plugins.jiratrigger.ErrorCode

/**
 * @author ceilfors
 */
enum ParameterErrorCode implements ErrorCode {

    FAILED_TO_RESOLVE('1')

    final String code

    ParameterErrorCode(String code) {
        this.code = code
    }
}