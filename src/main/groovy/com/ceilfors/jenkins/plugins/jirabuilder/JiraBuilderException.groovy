package com.ceilfors.jenkins.plugins.jirabuilder

/**
 * @author ceilfors
 */
class JiraBuilderException extends RuntimeException {

    private ErrorCode errorCode
    private Map attributes = [:]

    JiraBuilderException(ErrorCode errorCode) {
        this.errorCode = errorCode
    }

    JiraBuilderException(ErrorCode errorCode, Throwable cause) {
        super(cause)
        this.errorCode = errorCode
    }

    JiraBuilderException add(String key, String value) {
        attributes.put(key, value)
        return this
    }

    ErrorCode getErrorCode() {
        return errorCode
    }

    Map getAttributes() {
        return attributes
    }

    @Override
    String getMessage() {
        return "Class: ${errorCode.class.simpleName}, Name: ${errorCode.name()}, Code: ${errorCode.getCode()}, Attributes: ${attributes}"
    }
}
