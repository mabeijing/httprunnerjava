package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.Intf.PerformableIntf;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;

public class RequestWithOptionalArgs implements PerformableIntf {
    private final TStep __step_context;

    public RequestWithOptionalArgs(TStep step_context){
        __step_context = step_context;
    }

    public RequestWithOptionalArgs with_params(String paramsStr){
        __step_context.getRequest().getParams().update(new Params(paramsStr));
        return this;
    }

    public RequestWithOptionalArgs with_headers(String srcHeaders){
        __step_context.getRequest().getHeaders().update(new Headers(srcHeaders));
        return this;
    }

    public RequestWithOptionalArgs set_timeout(float timeout){
        __step_context.getRequest().setTimeout(timeout);
        return this;
    }

    public RequestWithOptionalArgs set_verify(boolean verify){
        __step_context.getRequest().setVerify(verify);
        return this;
    }

    public RequestWithOptionalArgs set_allow_redirects(boolean allow_redirects){
        __step_context.getRequest().setAllow_redirects(allow_redirects);
        return this;
    }

    public RequestWithOptionalArgs with_data(String data){
        __step_context.getRequest().setData(new LazyString(data));
        return this;
    }

    public RequestWithOptionalArgs with_json(String json){
        __step_context.getRequest().setReq_json(new ReqJson(json));
        return this;
    }

    @Override
    public TStep perform(){
        return this.__step_context;
    }

    public StepRequestValidation validate(){
        return new StepRequestValidation(this.__step_context);
    }


    public StepRequestExtraction extract(){
        return new StepRequestExtraction(this.__step_context);
    }

}
