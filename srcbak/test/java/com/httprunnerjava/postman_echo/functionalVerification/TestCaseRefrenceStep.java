package com.httprunnerjava.postman_echo.functionalVerification;

import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.model.Config;
import com.httprunnerjava.model.Step;
import com.httprunnerjava.model.component.RunRequest;
import com.httprunnerjava.model.component.RunTestCase;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestCaseRefrenceStep extends HttpRunner {

    private Config config = new Config("testcase reference")
            .variables("{'foo1':'config_bar1','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .export("['foo3']");

    private List<Step> teststeps = new ArrayList<Step>(){{
        add(new RunTestCase("request with testcase reference")
                .withVariables("{'foo1': 'testcase_ref_bar1', 'foo2': 'config_bar2'}")
                .call(SingleRequestStep.class)
                .export("['foo3']")
        );
    }};
}

