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
public class ExportTest extends HttpRunner {

    private Config config = new Config("config_name with variables,the viriables is $$foo: $foo1")
            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1'," +
                    "'expect_foo2': 'config_bar2', 'userId': '4Fxxxxx'}")
            .base_url("https://postman-echo.com")
            .verify(false);

    private List<Step> teststeps = new ArrayList<Step>(){{

        add(new RunTestCase("request with testcase reference")
                .withVariables("{'foo1': 'testcase_ref_bar1', 'expect_foo1': 'testcase_ref_bar1'}")
                .setupHook("${sleep(0.1)}")
                .call(SingleRequestStep.class)
                .teardownHook("${sleep(0.2)}")
                .export("['foo3']")
        );

        add(new RunRequest("get with params")
                .setupHook("setup_hooks()")
                .setupHook("{'accountId': '${getAccountId(3FXXXXXX)}'}")
                .setupHook("{'userId': '${getUserId($userId)}'}")
                .withVariables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
                .get("/get")
                .withParams("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v', " +
                        "'accountid': '$accountId','userId': '$userId', 'foo3': '$foo3'}")
                .withHeaders("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
                .teardownHook("teardown_hooks()")
                .extract()
                .withJmespath("body.args.foo2", "foo4")
                .validate()
                .assertEqual("status_code", 200)
                .assertEqual("body.args.foo1", "bar11")
                .assertEqual("body.args.sum_v", "1002")
                .assertEqual("body.args.foo2", "bar21")
                .assertEqual("body.args.foo3", "bar21")
        );

        add(new RunRequest("check extract")
                .get("/get/$foo4")
                .withParams("{'foo4': '$foo4'}")
                .validate()
                .assertEqual("body.args.foo4", "bar21")
        );

    }};
}

