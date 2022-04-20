package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Model.CallAble;
import com.httprunnerjava.Common.Model.TestCase;
import com.httprunnerjava.HttpRunner;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class TStep {
    private String name;
    private TRequest request;
    private Response response;
    private Class<? extends HttpRunner> testcase;
    //TODO：原版httprunner中，testcas还有可能是个str格式
    private String testcasestr;
    private CallAble testcasecall;
    private Variables variables;
    private Hooks setup_hooks = new Hooks();
    private Hooks teardown_hooks = new Hooks();
    private HashMap<String,String> extract;
    private Export export;
    private List<Validator> validators = new ArrayList<>();
    private List<String> validate_script;

    public TStep(String name){
        this.name = name;
        variables = new Variables();
        extract = new HashMap<String,String>();
    }

    public String toString(){
        //TODO:高优先级
        //用途1：当step解析错误时，会输出step的所有内容
        return this.name;
    }
}
