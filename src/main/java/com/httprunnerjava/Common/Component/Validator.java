package com.httprunnerjava.Common.Component;

import lombok.Data;

@Data
public class Validator {
    private String check_item;
    private Object expect_value;
    private String message;
    private String comparator;

    public Validator(String comparator,String check_item,Object expect_value,String message){
        this.comparator = comparator;
        this.check_item = check_item;
        this.expect_value = expect_value;
        this.message = message;
    }
}
