package com.httprunnerjava.postman_echo.exceptionVerification;

import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.model.Config;
import com.httprunnerjava.model.Step;
import com.httprunnerjava.model.component.RunRequest;

import java.util.ArrayList;
import java.util.List;

public class WrongValidate extends HttpRunner {

    private Config config = new Config("config_name with variables,the viriables is $$foo: $foo1")
            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .export("['foo3']");

    private List<Step> teststeps = new ArrayList<Step>(){{
        add(new Step(
                new RunRequest("get with params")
                        .withVariables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
                        .get("/get")
                        .withParams("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
                        .withHeaders("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
                        .extract()
                        .withJmespath("body.args.foo2", "foo3")
                        .validate()
                        .assertEqual("status_code", 201)
                        .assertEqual("body.args.foo1", "bar11")
                        .assertEqual("body.args.sum_v", "1002")
                        .assertEqual("body.args.foo2", "bar21")
        ));
    }};
}
