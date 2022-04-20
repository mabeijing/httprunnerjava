//package com.httprunnerjava.model.Tmodel;
//
//import com.httprunnerjava.HttpRunner;
//import com.httprunnerjava.model.component.atomsComponent.response.Export;
//import com.httprunnerjava.model.component.atomsComponent.request.Hooks;
//import com.httprunnerjava.model.component.atomsComponent.request.Variables;
//import com.httprunnerjava.model.component.atomsComponent.response.Validator;
//import com.httprunnerjava.model.component.intf.CallAble;
//import com.httprunnerjava.model.component.moleculesComponent.Response;
//import com.httprunnerjava.model.component.moleculesComponent.TRequest;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * @Author: ChuCan
// * @CreatedDate: 2022-04-02-20:36
// * @Description:
// */
//@Data
//public class TStep {
//
//    // 步骤的名字
//    private String name;
//
//    // 封装好的请求体
//    private TRequest request;
//
//    // http请求的响应
//    private Response response;
//
//    // 嵌套的testcase
//    private Class<? extends HttpRunner> testcase;
//
//    //TODO：原版httprunner中，testcas还有可能是个str格式，暂时并未实现
//    private String testcasestr;
//
//    // TODO：好像是实现错了的东西，后面确认下是否可以删除
//    private CallAble testcasecall;
//
//    // 步骤中的变量指定
//    private Variables variables;
//
//    // 钩子函数
//    private Hooks setupHooks = new Hooks();
//
//    private Hooks teardownHooks = new Hooks();
//
//    // 需要导出的键值对，key为导出的变量，value为导出的结构体位置
//    private HashMap<String, String> extract;
//
//    // TODO：待议
//    private Export export;
//
//    // 验证内容
//    private List<Validator> validators = new ArrayList<>();
//
//    // TODO：验证脚本，原版自带，暂不实现
//    private List<String> validate_script;
//
//    public TStep(String name) {
//        this.name = name;
//        variables = new Variables();
//        extract = new HashMap<>();
//    }
//
//    public String toString() {
//        // TODO:高优先级
//        // 用途1：当step解析错误时，会输出step的所有内容
//        return this.name;
//    }
//
//}
