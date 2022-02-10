package com.httprunnerjava.Common.Model;

import com.httprunnerjava.Common.Component.*;
import com.httprunnerjava.Common.Component.Intf.PerformableIntf;

public class Step implements PerformableIntf {
    private final TStep __step_context;

    public Step(PerformableIntf step_context){
        __step_context = step_context.perform();
    }

    public Step(StepRefCase stepRefCase){
        __step_context = stepRefCase.perform();
    }

    public TRequest request(){
        return __step_context.getRequest();
    }

    public Object testcase(){
        return __step_context.getTestcasecall();
    }

    public TStep perform(){
        return __step_context;
    }








}
