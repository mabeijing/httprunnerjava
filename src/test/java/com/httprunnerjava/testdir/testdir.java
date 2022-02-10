package com.httprunnerjava.testdir;

import com.httprunnerjava.Common.Model.Config;
import com.httprunnerjava.Common.Model.RunRequest;
import com.httprunnerjava.Common.Model.Step;
import com.httprunnerjava.HttpRunner;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.testng.*;
import org.testng.internal.ConstructorOrMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class testdir {

    public static void main(String[] args){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HttpRunner.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                if(method.getName().equals("new_test_start")){
                    System.out.println("123456");
                    System.out.println(o.getClass());
                    System.out.println(objects);
                }
                return null;
            }
        });

        HttpRunner userService = (HttpRunner)enhancer.create();


        MytestListener1 tla1 = new MytestListener1();
        MytestListener2 tla2 = new MytestListener2();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { HttpRunner.class});
        testng.addListener(tla1);
        testng.addListener(tla2);
        testng.run();

    }


    public static class MytestListener1 extends TestListenerAdapter {
        @Override
        public void onStart(ITestContext testContext) {
            super.onStart(testContext);
            System.out.println("onStart");
        }

        @Override
        public void onTestStart(ITestResult result) {
            super.onTestStart(result);
            System.out.println("onTestStart");
        }

    }

    public static class MytestListener2 implements IConfigurationListener {
        @Override
        public void beforeConfiguration(ITestResult tr) {
            // not implemented
            System.out.println("MytestListener2-001");
        }

        @Override
        public void beforeConfiguration(ITestResult tr, ITestNGMethod tm) {
            Config config = new Config("123123123123")
                    .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
                    .base_url("https://postman-echo.com")
                    .verify(false)
                    .export("['foo3']");                    ;
            List<Step> steps= new ArrayList<Step>(){
                {
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
                                    .assert_equal("body.args.sum_v", "3")
                                    .assert_equal("body.args.foo2", "bar21")
                    ));
                }};
            ((HttpRunner)tr.getInstance()).setConfig(config);
            ((HttpRunner)tr.getInstance()).setTeststeps(steps);
            System.out.println("MytestListener2-002");
        }


    }
}


//userService.setConfig(new Config("request methods testcase with functions")
//        .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
//        .base_url("https://postman-echo.com")
//        .verify(false)
//        .export("['foo3']"));
//
//        userService.setTeststeps(new ArrayList<Step>(){{
//        add(new Step(
//        new RunRequest("get with params")
//        .with_variables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
//        .get("/get")
//        .with_params("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
//        .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
//        .extract()
//        .with_jmespath("body.args.foo2", "foo3")
//        .validate()
//        .assert_equal("status_code", 200)
//        .assert_equal("body.args.foo1", "bar11")
//        .assert_equal("body.args.sum_v", "3")
//        .assert_equal("body.args.foo2", "bar21")
//        ));
//
//        add(new Step(
//        new RunRequest("post raw text")
//        .with_variables("{'foo1': 'bar12', 'foo3': 'bar32'}")
//        .post("/post")
//        .with_headers("{'User-Agent': 'HttpRunner/3.0', 'Content-Type': 'text/plain'}")
//        .with_data("This is expected to be sent back as part of response body: $foo1-$expect_foo2-$foo3.")
//        .validate()
//        .assert_equal("status_code", 200)
//        .assert_equal(
//        "body.data",
//        "This is expected to be sent back as part of response body: bar12-$expect_foo2-bar32."
//        )
//        .assert_equal("body.json", "NULL")
//        .assert_type_match("body.json", "NULL")
//        ));
//
//        add(new Step(
//        new RunRequest("post form data")
//        .with_variables("{'foo2': 'bar23'}")
//        .post("/post")
//        .with_headers("{'User-Agent': 'HttpRunner/3.0','Content-Type': 'application/x-www-form-urlencoded'}")
//        .with_data("foo1=$foo1&foo2=$foo2&foo3=$foo3")
//        .validate()
//        .assert_equal("status_code", 200)
//        .assert_equal("body.form.foo1", "$expect_foo1")
//        .assert_equal("body.form.foo2", "bar23")
//        .assert_equal("body.form.foo3", "bar21")
//        ));
//        }});
//
