package com.httprunnerjava.model.component;

import com.httprunnerjava.model.Step;
import com.httprunnerjava.model.component.atomsComponent.request.Variables;
import com.httprunnerjava.model.component.moleculesComponent.TRequest;

import java.util.Map;

public class RunRequest extends Step {
    public RunRequest(String name){
        super(name);
    }

    public RunRequest withVariables(String variables){
        Variables tmpVar = new Variables(variables);
        getVariables().update(tmpVar);
        return this;
    }

    public RunRequest withVariables(Map variables){
        getVariables().update(variables);
        return this;
    }

    public RunRequest setupHook(String hook){
        //TODO：
        // if assign_var_name: assign_var_name 含义未知，先不实现
        getSetupHooks().add(hook);
        return this;
    }

    public RunRequest setupHookNoThrowException(String hook){
        getSetupHooks().addNoThrowException(hook);
        return this;
    }

    public RunRequest teardownHook(String hook){
        //TODO：
        // if assign_var_name: assign_var_name 含义未知，先不实现
        getTeardownHooks().add(hook);
        return this;
    }

    public RunRequest teardownHookNoThrowException(String hook){
        getTeardownHooks().addNoThrowException(hook);
        return this;
    }

    public RequestWithOptionalArgs get(String url){
        setRequest(new TRequest("GET",url));
        return new RequestWithOptionalArgs(this);
    }

    public RequestWithOptionalArgs post(String url){
        setRequest(new TRequest("POST",url));
        return new RequestWithOptionalArgs(this);
    }

//    public StepRequestExtraction extract(){
//        return new StepRequestExtraction(this.stepContext);
//    }

}
