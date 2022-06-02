package com.httprunnerjava.exception;

public class VariableNotFound extends RuntimeException {
    public VariableNotFound(String errorMsg){
        super(errorMsg);
    }
}
