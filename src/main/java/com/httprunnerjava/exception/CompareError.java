package com.httprunnerjava.exception;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-09-1:21
 * @Description:
 */

public class CompareError extends RuntimeException {
    public CompareError(String msg){
        super(msg);
    }
}