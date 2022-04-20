package com.httprunnerjava.model.component.atomsComponent.response;

import lombok.Data;

@Data
public class Validator {
    private String checkItem;
    private Object expectValue;
    private String message;
    private String comparator;

    public Validator(String comparator,String checkItem,Object expectValue,String message){
        this.comparator = comparator;
        this.checkItem = checkItem;
        this.expectValue = expectValue;
        this.message = message;
    }
}

