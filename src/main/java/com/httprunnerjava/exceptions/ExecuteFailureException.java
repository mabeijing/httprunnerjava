package com.httprunnerjava.exceptions;

public class ExecuteFailureException extends RuntimeException {
    public ExecuteFailureException(String errorMsg){
        super(errorMsg);
    }
}
