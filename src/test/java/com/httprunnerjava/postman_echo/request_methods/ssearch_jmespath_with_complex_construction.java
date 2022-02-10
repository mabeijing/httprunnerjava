package com.httprunnerjava.postman_echo.request_methods;

import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ssearch_jmespath_with_complex_construction extends HttpRunner {

    private Config config = new Config("request methods testcase with variables")
            .variables("{'foo1':'testcase_config_bar1','foo2':'testcase_config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false);

    private List<Step> teststeps = new ArrayList<Step>() {
        {
            add(new Step(
                    new RunRequest("post form data using json2")
                            .with_variables(
                                    "{'foo2': 'bar23','instanceId':'QAZWSXEDC', 'userId' : 111222333}"
                            )
                            .post("/post")
                            .with_headers(
                                    "{'User-Agent': 'HttpRunner/3.0', 'Content-Type': 'application/json'}"
                            )
                            .with_json("{\"c\":\"0\",\"m\":\"\",\"d\":{$userId:[{\"in\":{\"inId\":2,\"name\":\"inId为2\",\"limit\":null},\"instance\":{\"teId\":null,\"inId\":6681,\"startTime\":1640593449000,\"expireTime\":1656345599000}},{\"in\":{\"inId\":3,\"name\":\"inId为3\",\"limit\":null},\"instance\":{\"teId\":null,\"inId\":6682,\"startTime\":1640593449001,\"expireTime\":1656345599001}}],\"111222334\":[{\"in\":{\"inId\":4,\"name\":\"inId为4\",\"limit\":null},\"instance\":{\"teId\":null,\"inId\":6682,\"startTime\":1640593449002,\"expireTime\":1656345599002}},{\"in\":{\"inId\":5,\"name\":\"inId为5\",\"limit\":null},\"instance\":{\"teId\":null,\"inId\":6686,\"startTime\":1640593449006,\"expireTime\":1656345599006}}]}}\n")
                            .extract()
                            .with_jmespath("body.data.d.$userId.0.instance.inId","instanceId")
                            .validate()
                            .assert_equal("status_code", 200)
            ));
        }
    };

}
