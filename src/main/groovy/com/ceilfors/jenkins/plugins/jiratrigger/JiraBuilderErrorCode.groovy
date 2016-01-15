package com.ceilfors.jenkins.plugins.jiratrigger

/**
 * @author ceilfors
 */
enum JiraBuilderErrorCode implements ErrorCode {

    JIRA_NOT_CONFIGURED("1")

    private String code

    JiraBuilderErrorCode(String code) {
        this.code = code
    }

    @Override
    String getCode() {
        return code
    }
}
