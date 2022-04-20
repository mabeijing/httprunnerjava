package com.httprunnerjava.exception;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-09-0:45
 * @Description:
 */
public class ParseError extends RuntimeException {
    public ParseError(String msg){
        super(msg);
    }
}
