package com.httprunnerjava.postman_echo.requestMethods;

import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.model.*;
import com.httprunnerjava.model.component.RunRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class request_with_functions_test extends HttpRunner {

    public Config config = new Config("request methods testcase with functions")
            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .export("['foo3']");

    public List<Step> teststeps = new ArrayList<Step>(){{
        add(new RunRequest("get with params")
                        .withVariables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
                        .get("/get")
                        .withParams("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
                        .withHeaders("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
                        .extract()
                        .withJmespath("body.args.foo2", "foo3")
                        .validate()
                        .assertEqual("status_code", 200)
                        .assertEqual("body.args.foo1", "bar11")
                        .assertEqual("body.args.sum_v", "1002")
                        .assertEqual("body.args.foo2", "bar21")
        );
    }};
}
