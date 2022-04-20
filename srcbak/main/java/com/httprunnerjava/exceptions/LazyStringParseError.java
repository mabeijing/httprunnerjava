package com.httprunnerjava.exceptions;

public class LazyStringParseError extends RuntimeException {
    public LazyStringParseError(String errorMsg){
        super(errorMsg);
    }
}
