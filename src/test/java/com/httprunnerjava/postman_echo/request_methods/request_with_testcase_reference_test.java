package com.httprunnerjava.postman_echo.request_methods;

import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.RunTestCase;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class request_with_testcase_reference_test extends HttpRunner {

    private Config config = new Config("request methods testcase: reference testcase")
            .variables("{'foo1':'testsuite_config_bar1','expect_foo1':'testsuite_config_bar1','expect_foo2':'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false);

    private List<Step> teststeps = new ArrayList<Step>() {
        {
            add(new Step(
                    new RunTestCase("request with functions")
                            .with_variables("{'foo1': 'testcase_ref_bar1', 'expect_foo1': 'testcase_ref_bar1'}")
                            .setup_hook("${sleep(0.1)}")
                            .call(request_with_functions_test.class)
                            .teardown_hook("${sleep(0.2)}")
                            .export("['foo3']")
            ));

            add(new Step(
                    new RunRequest("post form data")
                            .with_variables("{'foo1': 'bar1'}")
                            .post("/post")
                            .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'," +
                                    "'Content-Type': 'application/x-www-form-urlencoded'}")
                            .with_data("foo1=$foo1&foo2=$foo3")
                            .validate()
                            .assert_equal("status_code", 200)
                            .assert_equal("body.form.foo1", "bar1")
                            .assert_equal("body.form.foo2", "bar21")
            ));
        }
    };
}

