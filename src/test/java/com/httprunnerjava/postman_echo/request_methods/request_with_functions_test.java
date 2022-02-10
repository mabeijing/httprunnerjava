package com.httprunnerjava.postman_echo.request_methods;

import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class request_with_functions_test extends HttpRunner {

    private Config config = new Config("request methods testcase with functions")
            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .export("['foo3']");

    private List<Step> teststeps = new ArrayList<Step>(){{
        add(new Step(
                new RunRequest("get with params")
                        .with_variables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
                        .get("/get")
                        .with_params("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
                        .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
                        .extract()
                        .with_jmespath("body.args.foo2", "foo3")
                        .validate()
                        .assert_equal("status_code", 200)
                        .assert_equal("body.args.foo1", "bar11")
                        .assert_equal("body.args.sum_v", "1002")
                        .assert_equal("body.args.foo2", "bar21")
        ));

        add(new Step(
                new RunRequest("post raw text")
                        .with_variables("{'foo1': 'bar12', 'foo3': 'bar32'}")
                        .post("/post")
                        .with_headers("{'User-Agent': 'HttpRunner/3.0', 'Content-Type': 'text/plain'}")
                        .with_data("This is expected to be sent back as part of response body: $foo1-$expect_foo2-$foo3.")
                        .validate()
                        .assert_equal("status_code", 200)
                        .assert_equal(
                                "body.data",
                                "This is expected to be sent back as part of response body: bar12-$expect_foo2-bar32."
                        )
                        .assert_equal("body.json", "NULL")
                        .assert_type_match("body.json", "NULL")
        ));

        add(new Step(
                new RunRequest("post form data")
                        .with_variables("{'foo2': 'bar23'}")
                        .post("/post")
                        .with_headers("{'User-Agent': 'HttpRunner/3.0','Content-Type': 'application/x-www-form-urlencoded'}")
                        .with_data("foo1=$foo1&foo2=$foo2&foo3=$foo3")
                        .validate()
                        .assert_equal("status_code", 200)
                        .assert_equal("body.form.foo1", "$expect_foo1")
                        .assert_equal("body.form.foo2", "bar23")
                        .assert_equal("body.form.foo3", "bar21")
        ));
    }};
}
