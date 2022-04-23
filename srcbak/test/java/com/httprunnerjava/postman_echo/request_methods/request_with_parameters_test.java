package com.httprunnerjava.postman_echo.request_methods;

import com.httprunnerjava.Common.Component.TStep;
import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.Parse;
import lombok.Data;
import org.testng.annotations.DataProvider;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;

@Data
public class request_with_parameters_test extends HttpRunner {

    private Config config = new Config("request methods testcase: validate with parameters")
            .variables("{'app_version':'f1'}")
            .base_url("https://postman-echo.com")
            .verify(false);

    private List<Step> teststeps = new ArrayList<Step>(){
        {
            add(new Step(
                    new RunRequest("get with params")
                            .with_variables("{'foo1': '$username', 'foo2': '$password', 'sum_v': '${sum_two_double(1, $app_version)}'}")
                            .get("/get")
                            .with_params("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
                            .with_headers("{'User-Agent': '$user_agent,$app_version'}")
                            .extract()
                            .with_jmespath("body.args.foo2", "session_foo2")
                            .validate()
                            .assert_equal("status_code", 200)
                            .assert_equal("body.args.sum_v", "${sum_two_double(1, $app_version)}")
            ));
        }
    };

    public static List<Map<String,Object>> getParams() {
        return Parse.parse_parameters(new HashMap<String, Object>() {{
                                          put("user_agent", Arrays.asList("iOS/10.1", "iOS/10.2"));
                                          put("username-password", "${parameterize(request_methods/account.csv)}");
                                          put("app_version", "${get_app_version()}");
                                      }}
        );
    }

    @Override
    @DataProvider(name="HrunDataProvider")
    public Iterator<Object[]> createData(){
        List<Object[]> users = new ArrayList<>();
        for (TStep u : this.get__teststeps()) {
            for(Map<String,Object> each : getParams()){
                users.add(new Object[]{u,each});
            }
        }

        return users.iterator();
    }

}
