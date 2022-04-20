package com.httprunnerjava.exception;

public class ParseError extends RuntimeException {
    public ParseError(String msg){
        super(msg);
    }
}
