package com.nocode.admin.exception;

/**
 * Code generation exception
 */
public class CodeGenerateException extends RuntimeException {

    public CodeGenerateException(String message) {
        super(message);
    }

    public CodeGenerateException(String message, Throwable cause) {
        super(message, cause);
    }
}