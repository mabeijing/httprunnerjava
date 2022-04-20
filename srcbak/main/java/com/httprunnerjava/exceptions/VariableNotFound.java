package com.httprunnerjava.exceptions;

public class VariableNotFound extends RuntimeException {
    public VariableNotFound(String errorMsg){
        super(errorMsg);
    }
}
