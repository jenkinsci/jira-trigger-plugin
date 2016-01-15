package com.ceilfors.jenkins.plugins.jiratrigger

/**
 * @author ceilfors
 */
enum JiraTriggerErrorCode implements ErrorCode {

    JIRA_NOT_CONFIGURED("1")

    private String code

    JiraTriggerErrorCode(String code) {
        this.code = code
    }

    @Override
    String getCode() {
        return code
    }
}
