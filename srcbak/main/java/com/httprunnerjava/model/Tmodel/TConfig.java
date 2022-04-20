//package com.httprunnerjava.model.Tmodel;
//
//import com.httprunnerjava.model.component.atomsComponent.response.Export;
//import com.httprunnerjava.model.component.atomsComponent.request.Variables;
//import com.httprunnerjava.model.lazyLoading.LazyString;
//import lombok.Data;
//
///**
// * @Author: ChuCan
// * @CreatedDate: 2022-04-07-1:25
// * @Description:
// */
//
//@Data
//public class TConfig {
//
//
//    /*TODO:
//    private Hooks setup_hooks;
//    private Hooks teardown_hooks;
//     */
//
//
//    // 一个HttpRunner中仅有一个Config，该名字即为整个测试类的名字
//    private LazyString name;
//
//    // TODO: 原版HttpRunner的变量，需要考虑是否删除
//    private Boolean verify;
//
//    // 用例的全局url域名
//    private LazyString baseUrl;
//
//    // 用例的全局参数，Config中参数的优先级低于用例步骤中的优先级
//    private Variables variables;
//
//    // 参数化选项不再支持，直接使用testng的参数化方式即可
//    // private Parameters parameters;
//
//    // 用例执行后需要导出的内容，详细请查看test目录下的示例
//    private Export export;
//
//    // TODO：
//    //  文件路径，待实现
//    private LazyString path;
//
//    // TODO:
//    //  执行权重，待实现
//    private Integer weight;
//
//    // 是否开启proxy模式，开启后会自动设置127.0.0.0:8888代理，所有的请求都会经过代理转发
//    private Boolean isProxy = false;
//
//    // 单个用例执行出错后是否继续执行
//    private Boolean resumeAfterException = false;
//
//    // TODO：
//    //  超时时间，待实现
//    private Integer timeOut;
//
//    public TConfig(LazyString name, LazyString baseUrl, Boolean verify, Variables variables, Export export, LazyString path,
//                   int weight) {
//        this.name = name;
//        this.baseUrl = baseUrl;
//        this.verify = verify;
//        this.variables = variables;
//        this.export = export;
//        this.path = path;
//        this.weight = weight;
//    }
//
//}
