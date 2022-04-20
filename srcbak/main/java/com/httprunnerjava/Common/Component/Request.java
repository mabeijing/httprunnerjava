package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.Enum.MethodEnum;
import com.httprunnerjava.Common.Component.Intf.HttpBody;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import lombok.Data;

@Data
public class Request implements HttpBody {

    private MethodEnum method;
    private LazyString url;
    private Params params;
    private Headers headers;
    //TODO:
//    private ReqJson req_json;
//    private PostData postData;
    
//    private Cookie cookie;
    private Integer timeout;
    private Boolean allow_redirects;
    private Boolean verify;
//    private Object upload;

    @Override
    public String toString(){
        String result = "" + method.getMethod()
                +url.getEvalString() + params.toString() +
                headers.toString();
        return "this is request";
    }

}
