package com.httprunnerjava.model.runningData;

import com.httprunnerjava.model.component.atomsComponent.request.Headers;
import com.httprunnerjava.model.component.intf.reqOrResp;

public class ResponseData implements reqOrResp {
    private Integer status_code;
    private Headers headers;
    private String cookie;
    private String encoding;
    private String content_type;
    private String body;


    public ResponseData(Integer status_code, Headers headers, String cookie, String encoding,
                        String content_type, String body){
        this.status_code = status_code;
        this.headers = headers;
        this.cookie = cookie;
        this.encoding = encoding;
        this.content_type = content_type;
        this.body = body;
    }

    public String toString(){
        StringBuffer result = new StringBuffer()
                .append("status_code  : " + status_code + "\n")
                .append("headers      : " + headers.toString() + "\n")
                .append("cookies      : " + cookie.toString() + "\n")
                .append("encoding     : " + encoding + "\n")
                .append("content_type : " + content_type + "\n")
                .append("body         : " + body + "\n");

        return result.toString();
    }
}
