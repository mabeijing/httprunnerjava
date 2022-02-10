package com.httprunnerjava.Common.Model;

import com.httprunnerjava.Common.Component.Export;
import com.httprunnerjava.Common.Component.TStep;

public class StepRefCase {

    private TStep __step_context;

    public StepRefCase(TStep step_context){
        __step_context = step_context;
    }

    public StepRefCase teardown_hook(String hook){
        //TODO：if assign_var_name:

        __step_context.getTeardown_hooks().add(hook);

        return this;
    }

    public StepRefCase export(String var_name){
        Export export = new Export(var_name);
        __step_context.setExport(export);
        return this;
    }

    public TStep perform(){
        return this.__step_context;
    }
}
