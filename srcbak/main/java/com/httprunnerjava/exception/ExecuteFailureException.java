package com.httprunnerjava.exception;

public class ExecuteFailureException extends RuntimeException {
    public ExecuteFailureException(String errorMsg){
        super(errorMsg);
    }
}
