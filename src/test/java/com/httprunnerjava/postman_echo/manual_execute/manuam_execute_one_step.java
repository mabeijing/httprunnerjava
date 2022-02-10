package com.httprunnerjava.postman_echo.manual_execute;

import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class manuam_execute_one_step extends HttpRunner {

    private Config config = new Config("request methods testcase with functions")
            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .withCatchAllExpection(true)
            .export("['foo3']");

    private List<Step> teststeps = new ArrayList<Step>(){{
        add(new Step(
                new RunRequest("step1")
                        .with_variables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
                        .get("/get")
                        .with_params("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v','userId':'$userId'}")
                        .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
                        .extract()
                        .with_jmespath("body.args.foo2", "foo3")
                        .validate()
                        .assert_equal("status_code", 200)
                        .assert_equal("body.args.foo1", "bar11")
                        .assert_equal("body.args.sum_v", "3.0")
                        .assert_equal("body.args.foo2", "bar21")
        ));

        add(new Step(
                new RunRequest("step2")
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
    }};

    public static void main(String[] args){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId","username123456");
        //执行step1
        HttpRunner httpRunner = manual_execute_single_test_start(manuam_execute_one_step.class, 0, params);
        httpRunner.getStep_datas().stream().forEach( e -> {
            System.out.println(e.getExport_vars().toString());
        });
    }

}
