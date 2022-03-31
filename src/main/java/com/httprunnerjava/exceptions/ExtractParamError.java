package com.httprunnerjava.exceptions;

public class ExtractParamError extends RuntimeException {
    public ExtractParamError(String errorMsg){
        super(errorMsg);
    }
}
