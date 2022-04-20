package com.httprunnerjava.model.component;

import com.httprunnerjava.model.Step;

public class StepRequestExtraction extends Step{

    public StepRequestExtraction(Step stepContext){
        super(stepContext);
    }

    public StepRequestExtraction withJmespath(String jmesPath, String varName){
        getExtract().put(varName,jmesPath);
        return this;
    }

    public StepRequestValidation validate(){
        return new StepRequestValidation(this);
    }
}
