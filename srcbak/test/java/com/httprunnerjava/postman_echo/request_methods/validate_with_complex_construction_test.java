package com.httprunnerjava.postman_echo.request_methods;

import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class validate_with_complex_construction_test extends HttpRunner {

    private Config config = new Config("request methods testcase: validate with functions")
            .variables("{'foo1':'session_bar1'}")
            .base_url("https://postman-echo.com")
            .verify(false);

    private List<Step> teststeps = new ArrayList<Step>() {
        {
            add(new Step(
                    new RunRequest("post form data using json 1")
                            .with_variables(
                                    "{'foo2': 'bar23','jsondata':[{'key1':'value1','key2':'value2'},{'key11':'value11','key22':'value22'},{'key111':'value111','key222':'value222'}]}"
                            )
                            .post("/post")
                            .with_headers(
                                    "{'Content-Type': 'application/json'}"
                            )
                            .with_json("$jsondata")
                            .validate()
                            .assert_equal("status_code", 200)
                            .list_contains("body.json","[{'key1':'value1','key2':'value2'},{'key11':'value11','key22':'value22'},{'key111':'value111','key222':'value222'}]")

            ));

            add(new Step(
                    new RunRequest("post form data using json 2")
                            .with_variables(
                                    "{'foo2': 'bar23','jsondata':[11,22,33,44,55,66,77,88,99]}"
                            )
                            .post("/post")
                            .with_headers(
                                    "{'Content-Type': 'application/json'}"
                            )
                            .with_json("$jsondata")
                            .validate()
                            .assert_equal("status_code", 200)
                            .list_contains("body.json","[11,22,33,44,55,66,77,88,99]")
                            .not_list_empty("body.json")
            ));

            add(new Step(
                    new RunRequest("post form data using json 3")
                            .with_variables(
                                    "{'foo2': 'bar23','jsondata':[]}"
                            )
                            .post("/post")
                            .with_headers(
                                    "{'Content-Type': 'application/json'}"
                            )
                            .with_json("$jsondata")
                            .validate()
                            .assert_equal("status_code", 200)
                            .list_empty("body.data")
            ));

            add(new Step(
                    new RunRequest("post form data using json 4")
                            .with_variables(
                                    "{'userId': '123456789','rId':'2'}"
                            )
                            .post("/post")
                            .with_headers(
                                    "{'Content-Type': 'application/json'}"
                            )
                            .with_json("{\n" +
                                    "    \"userIds\":[$userId],\n" +
                                    "    \"id\":$rId,\n" +
                                    "    \"orgId\":'${getAccountId($userId)}'\n" +
                                    "}")
                            .validate()
                            .assert_equal("status_code", 200)
            ));
        }
    };
}
