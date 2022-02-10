package com.httprunnerjava.Common.Component;

public class StepRequestExtraction {

    public TStep __step_context;

    public StepRequestExtraction(TStep step_context){
        this.__step_context = step_context;
    }

    public StepRequestExtraction with_jmespath(String jmes_path, String var_name){
        this.__step_context.getExtract().put(var_name,jmes_path);
        return this;
    }

    public StepRequestValidation validate(){
        return new StepRequestValidation(this.__step_context);
    }

    public TStep perform(){
        return this.__step_context;
    }
}
