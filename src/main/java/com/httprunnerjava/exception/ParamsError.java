package com.httprunnerjava.exception;

public class ParamsError extends RuntimeException{
    public ParamsError(String errorMsg){
        super(errorMsg);
    }
}
