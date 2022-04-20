package com.httprunnerjava.exception;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-09-0:45
 * @Description:
 */
public class UnknowError extends RuntimeException {
    public UnknowError(String msg){
        super(msg);
    }
}
