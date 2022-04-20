package com.httprunnerjava.Common.Model;

import com.httprunnerjava.Common.Component.TStep;
import com.httprunnerjava.Common.Component.Variables;
import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTestCase {
    private static final Logger logger = LoggerFactory.getLogger(RunTestCase.class);

    private final TStep __step_context;

    public RunTestCase(String name){
        __step_context = new TStep(name);
    }

    public RunTestCase with_variables(String variables){
        Variables tmpVar = new Variables(variables);
        __step_context.getVariables().update(tmpVar);
        return this;
    }

    public RunTestCase setup_hook(String hook){
        //TODO：
        // if assign_var_name: assign_var_name 含义未知，先不实现
        __step_context.getSetup_hooks().add(hook);
        return this;
    }

    public RunTestCase setup_hook_no_throw_exception(String hook){
        __step_context.getSetup_hooks().addNoThrowException(hook);
        return this;
    }
    public StepRefCase call(Class<? extends HttpRunner> testcase){
        if(testcase.getSuperclass().getName().contains("HttpRunner")) {
            __step_context.setTestcase(testcase);
            return new StepRefCase(__step_context);
        }else{
            HrunExceptionFactory.create("E0068");
        }

        return null;
    }

    public TStep perform() {
        return __step_context;
    }
}
