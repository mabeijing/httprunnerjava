//package com.httprunnerjava.postman_echo.request_methods;
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
//public class validate_with_functions_test extends HttpRunner {
//
//    private Config config = new Config("request methods testcase: validate with functions")
//            .variables("{'foo1':'session_bar1'}")
//            .base_url("https://postman-echo.com")
//            .verify(false);
//
//    private List<Step> teststeps = new ArrayList<Step>() {
//        {
//            add(new Step(
//                    new RunRequest("get with params")
//                            .with_variables("{'foo1': 'bar1', 'foo2': 'session_bar2','sum_v': '${sum_two(1, 2)}'}")
//                            .get("/get")
//                            .with_params("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
//                            .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
//                            .extract()
//                            .with_jmespath("body.args.foo2", "session_foo2")
//                            .validate()
//                            .assert_equal("status_code", 200)
//                            .assert_equal("body.args.sum_v", "3")
//            ));
//        }
//    };
//}
