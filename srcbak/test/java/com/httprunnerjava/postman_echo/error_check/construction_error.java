// package com.httprunnerjava.postman_echo.error_check;
//
//import com.httprunnerjava.Common.Model.Config;
//import com.httprunnerjava.Common.Model.RunRequest;
//import com.httprunnerjava.Common.Model.Step;
//import com.httprunnerjava.HttpRunner;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//public class construction_error extends HttpRunner {
//
//    //本case主要用于验证存在一些结构化错误无法解析时，日志信息是否准确
//    private Config config = new Config("error check")
//            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'")
//            .base_url("https://postman-echo.com")
//            .verify(false)
//            .export("['foo3']");
//
//    private List<Step> teststeps = new ArrayList<Step>() {{
//        add(new Step(
//                new RunRequest("validate failed")
//                        .with_variables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
//                        .get("/get")
//                        .with_params("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
//                        .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
//                        .extract()
//                        .with_jmespath("body.args.foo2", "foo3")
//                        .validate()
//                        .assert_equal("status_code", 404)
//                        .assert_equal("body.args.foo1", "bar111")
//                        .assert_equal("body.args.sum_v", "31")
//                        .assert_equal("body.args.foo2", "bar211")
//        ));
//    }};
//}
