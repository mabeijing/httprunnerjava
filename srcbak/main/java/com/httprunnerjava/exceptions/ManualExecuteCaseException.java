package com.httprunnerjava.exceptions;

public class ManualExecuteCaseException extends RuntimeException {
    public ManualExecuteCaseException(String errorMsg){
        super(errorMsg);
    }
}
