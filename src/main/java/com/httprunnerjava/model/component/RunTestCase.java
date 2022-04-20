package com.httprunnerjava.model.component;

import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.exception.HrunExceptionFactory;
import com.httprunnerjava.model.Step;
import com.httprunnerjava.model.component.atomsComponent.request.Variables;

public class RunTestCase extends Step{

    public RunTestCase(String name){
        super(name);
    }

    public RunTestCase withVariables(String variables){
        Variables tmpVar = new Variables(variables);
        getVariables().update(tmpVar);
        return this;
    }

    public RunTestCase setupHook(String hook){
        //TODO：
        // if assign_var_name: assign_var_name 含义未知，先不实现
        getSetupHooks().add(hook);
        return this;
    }

    public RunTestCase setupHookNoThrowException(String hook){
        getSetupHooks().addNoThrowException(hook);
        return this;
    }
    public StepRefCase call(Class<? extends HttpRunner> testcase){
        if(testcase.getSuperclass().getName().contains("HttpRunner")) {
            setTestcase(testcase);
            return new StepRefCase(this);
        }else{
            HrunExceptionFactory.create("E0068");
        }

        return null;
    }
}
