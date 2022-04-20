package com.httprunnerjava.postman_echo.exceptionVerification;

import com.httprunnerjava.HttpRunner;
import com.httprunnerjava.model.Config;
import com.httprunnerjava.model.Step;
import lombok.Getter;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-14-16:46
 * @Description: step为空时抛出异常，但是这里存在一个问题是在beforeclass中抛出的异常，testMethod还是会继续执行，没有在testng官网找到终止class的方法
 */
@Getter
@Test(alwaysRun=true)
public class NoStep extends HttpRunner {
    private Config config = new Config("request methods testcase with functions")
            .variables("{'foo1':'config_bar1','foo2':'config_bar2','expect_foo1':'config_bar1','expect_foo2': 'config_bar2'}")
            .base_url("https://postman-echo.com")
            .verify(false)
            .export("['foo3']");

    private List<Step> teststeps = new ArrayList<Step>();

}
