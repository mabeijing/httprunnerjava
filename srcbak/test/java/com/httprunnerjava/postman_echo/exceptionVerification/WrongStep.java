package com.httprunnerjava.postman_echo.exceptionVerification;

import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.model.Config;
import com.httprunnerjava.model.Step;
import com.httprunnerjava.model.component.RunRequest;

import java.util.ArrayList;
import java.util.List;

public class WrongStep extends HttpRunner {

    private Config config = new Config("config_name with variables,the viriables is $$foo: $foo1")
            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .export("['foo3']");

    private List<Step> teststeps = new ArrayList<Step>(){{
        add(new Step("WrongStep"));
    }};
}
