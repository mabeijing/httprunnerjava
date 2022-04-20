package com.httprunnerjava.model.component;

import com.httprunnerjava.model.Step;
import com.httprunnerjava.model.component.atomsComponent.response.Export;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-08-0:27
 * @Description:
 */
public class StepRefCase extends Step{
    public StepRefCase(Step stepContext){
        super(stepContext);
    }

    public StepRefCase teardownHook(String hook){
        //TODO：if assign_var_name:
        getTeardownHooks().add(hook);
        return this;
    }

    public StepRefCase teardownHookNoThrowException(String hook){
        getTeardownHooks().addNoThrowException(hook);
        return this;
    }

    public StepRefCase export(String varName){
        Export export = new Export(varName);
        setExport(export);
        return this;
    }
}
