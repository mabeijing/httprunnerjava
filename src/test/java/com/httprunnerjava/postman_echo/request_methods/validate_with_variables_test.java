package com.httprunnerjava.postman_echo.request_methods;

import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class validate_with_variables_test extends HttpRunner {

    private Config config = new Config("request methods testcase: validate with variables")
            .variables("{'foo1':'session_bar1'}")
            .base_url("https://postman-echo.com")
            .verify(false);

    private List<Step> teststeps = new ArrayList<Step>() {
        {
            add(new Step(
                    new RunRequest("get with params")
                            .with_variables("{'foo1': 'bar1', 'foo2': 'session_bar2'}")
                            .get("/get")
                            .with_params("{'foo1': '$foo1', 'foo2': '$foo2'}")
                            .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
                            .extract()
                            .with_jmespath("body.args.foo2", "session_foo2")
                            .validate()
                            .assert_equal("status_code", 200)
                            .assert_equal("body.args.foo1", "$foo1")
                            .assert_equal("body.args.foo2", "$foo2")
            ));

            add(new Step(
                    new RunRequest("post raw text")
                            .with_variables("{'foo1': 'hello world', 'foo3': '$session_foo2'}")
                            .post("/post")
                            .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}','Content-Type': 'text/plain'}")
                            .with_data(
                                    "This is expected to be sent back as part of response body: $foo1-$foo3."
                            )
                            .validate()
                            .assert_equal("status_code", 200)
                            .assert_equal(
                                    "body.data",
                                    "This is expected to be sent back as part of response body: hello world-$foo3."
                                    )
            ));

            add(new Step(
                    new RunRequest("post form data")
                            .with_variables("{'foo1': 'bar1', 'foo2': 'bar2'}")
                            .post("/post")
                            .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}', 'Content-Type': 'application/x-www-form-urlencoded'}")
                            .with_data("foo1=$foo1&foo2=$foo2")
                            .validate()
                            .assert_equal("status_code", 200)
                            .assert_equal("body.form.foo1", "$foo1")
                            .assert_equal("body.form.foo2", "$foo2")
            ));
        }
    };

}


