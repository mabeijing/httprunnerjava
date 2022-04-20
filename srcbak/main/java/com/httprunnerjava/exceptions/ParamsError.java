package com.httprunnerjava.exceptions;

public class ParamsError extends RuntimeException{
    public ParamsError(String errorMsg){
        super(errorMsg);
    }
}
