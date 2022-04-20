package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.Intf.PerformableIntf;

public class StepRequestValidation implements PerformableIntf {
    private final TStep __step_context;

    public StepRequestValidation(TStep step_context){
        this.__step_context = step_context;
    }

    public StepRequestValidation assert_equal(String jmes_path, Object expected_value){
        return assert_equal(jmes_path,expected_value,null);
    }

    public StepRequestValidation assert_equal(String jmes_path,Object expected_value,String message) {
        this.__step_context.getValidators().add(new Validator("equals",jmes_path,expected_value,message));
        return this;
    }

    public StepRequestValidation list_empty(String jmes_path){
        return list_empty(jmes_path, null);
    }

    public StepRequestValidation list_empty(String jmes_path, String message) {
        this.__step_context.getValidators().add(new Validator("list_empty",jmes_path, null, message));
        return this;
    }

    public StepRequestValidation not_list_empty(String jmes_path){
        return not_list_empty(jmes_path, null);
    }

    public StepRequestValidation not_list_empty(String jmes_path, String message) {
        this.__step_context.getValidators().add(new Validator("not_list_empty",jmes_path, null, message));
        return this;
    }

    public StepRequestValidation list_contains(String jmes_path, Object expected_value){
        return list_contains(jmes_path,expected_value, null);
    }

    public StepRequestValidation list_contains(String jmes_path, Object expected_value,String message) {
        this.__step_context.getValidators().add(new Validator("list_contains",jmes_path,expected_value,message));
        return this;
    }

    public StepRequestValidation assert_type_match(String jmes_path,Object expected_value){
        return type_match(jmes_path,expected_value,null);
    }

    public StepRequestValidation type_match(String jmes_path,Object expected_value,String message) {
        this.__step_context.getValidators().add(new Validator("type_match",jmes_path,expected_value,message));
        return this;
    }


    @Override
    public TStep perform() {
        return __step_context;
    }

}
