package com.httprunnerjava.model.runningData;

import com.httprunnerjava.model.Enum.MethodEnum;
import com.httprunnerjava.model.component.atomsComponent.request.Headers;
import com.httprunnerjava.model.component.intf.reqOrResp;

public class RequestData implements reqOrResp {
    MethodEnum method;
    String url;
    Headers headers;
    String cookie;
    //这里的body可能不只是string，还可能是二进制数据呢
    String body;

    public RequestData(String method, String url, Headers headers, String cookie, String body){
        this.method = MethodEnum.valueOf(method);
        this.url = url;
        this.headers = headers;
        this.cookie = cookie;
        this.body = body;
    }

    public String toString(){
        return "method       : " + method.getMethod() + "\n" +
                "url          : " + url + "\n" +
                "headers      : " + headers.toString() + "\n" +
                "cookies      : " + cookie.toString() + "\n" +
                "body         : " + body + "\n";
    }

}
