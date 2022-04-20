package com.httprunnerjava.exception;

public class ValidationFailureException extends AssertionError {
    public ValidationFailureException(String errorMsg){
        super(errorMsg);
    }
}
