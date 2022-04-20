package com.httprunnerjava.exception;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-10-1:10
 * @Description:
 */
public class VariableNotFound extends RuntimeException {
    public VariableNotFound(String errorMsg){
        super(errorMsg);
    }
}
