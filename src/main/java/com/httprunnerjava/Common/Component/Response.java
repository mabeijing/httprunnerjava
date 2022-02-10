package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.Intf.HttpBody;

public class Response implements HttpBody {

    @Override
    public String toString(){
        return "this is response";
    }
}
