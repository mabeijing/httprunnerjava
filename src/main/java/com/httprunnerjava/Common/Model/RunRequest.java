package com.httprunnerjava.Common.Model;

import com.httprunnerjava.Common.Component.*;
import com.httprunnerjava.Common.Component.Intf.PerformableIntf;

import java.util.Map;

public class RunRequest implements PerformableIntf {
    private final TStep __step_context;

    public RunRequest(String name){
        __step_context = new TStep(name);
    }

    public RunRequest with_variables(String variables){
        Variables tmpVar = new Variables(variables);
        __step_context.getVariables().update(tmpVar);
        return this;
    }

    public RunRequest with_variables(Map variables){
        __step_context.getVariables().update(variables);
        return this;
    }

    public RunRequest setup_hook(String hook){
        //TODO：
        // if assign_var_name: assign_var_name 含义未知，先不实现
        __step_context.getSetup_hooks().add(hook);
        return this;
    }

    public RunRequest setup_hook_no_throw_exception(String hook){
        __step_context.getSetup_hooks().addNoThrowException(hook);
        return this;
    }


    public RunRequest teardown_hook(String hook){
        //TODO：
        // if assign_var_name: assign_var_name 含义未知，先不实现
        __step_context.getTeardown_hooks().add(hook);
        return this;
    }

    public RunRequest teardown_hook_no_throw_exception(String hook){
        __step_context.getTeardown_hooks().addNoThrowException(hook);
        return this;
    }

    public RequestWithOptionalArgs get(String url){
        __step_context.setRequest(new TRequest("GET",url));
        return new RequestWithOptionalArgs(__step_context);
    }

    public RequestWithOptionalArgs post(String url){
        __step_context.setRequest(new TRequest("POST",url));
        return new RequestWithOptionalArgs(__step_context);
    }

//    public StepRequestExtraction extract(){
//        return new StepRequestExtraction(this.__step_context);
//    }

    @Override
    public TStep perform() {
        return __step_context;
    }

}
