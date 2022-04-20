package com.httprunnerjava.exceptions;

public class ValidationFailureException extends AssertionError {
    public ValidationFailureException(String errorMsg){
        super(errorMsg);
    }
}
