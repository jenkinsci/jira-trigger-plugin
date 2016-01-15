package com.ceilfors.jenkins.plugins.jiratrigger

/**
 * @author ceilfors
 */
class JiraTriggerException extends RuntimeException {

    private ErrorCode errorCode
    private Map attributes = [:]

    JiraTriggerException(ErrorCode errorCode) {
        this.errorCode = errorCode
    }

    JiraTriggerException(ErrorCode errorCode, Throwable cause) {
        super(cause)
        this.errorCode = errorCode
    }

    JiraTriggerException add(String key, String value) {
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
