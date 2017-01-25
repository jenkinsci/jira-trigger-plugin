package com.ceilfors.jenkins.plugins.jiratrigger

/**
 * @author ceilfors
 */
class JiraTriggerException extends RuntimeException {

    final ErrorCode errorCode
    final Map attributes = [:]

    JiraTriggerException(ErrorCode errorCode) {
        this.errorCode = errorCode
    }

    JiraTriggerException(ErrorCode errorCode, Throwable cause) {
        super(cause)
        this.errorCode = errorCode
    }

    JiraTriggerException add(String key, String value) {
        attributes.put(key, value)
        this
    }

    @Override
    String getMessage() {
        "Class: ${errorCode.class.simpleName}, Name: ${errorCode.name()}, " +
                "Code: ${errorCode.code}, Attributes: ${attributes}"
    }
}
