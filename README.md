
# 欢迎使用 HttpRunner For Java

------

HttpRunner是一款优秀的接口自动化测试框架，目前已经演变了三个大版本，HttpRunner目前是python语言开发（目前也有GoLang版本在不断迭代），JAVA下目前并没有类似的工具可用。HttpRunner For Java项目主要基于HttpRunner的3.x版本，实现了JAVA版本的HttpRunner，继承了原版几乎所有的优秀设计。

## 核心特性
> * 支持API接口的多种请求方法，包括 GET/POST/HEAD/PUT/DELETE 等（目前实现了前两种，其他正在开发中）
> * 测试用例描述方式具有表现力，可采用简洁的方式描述输入参数和预期输出结果
> * 接口测试用例具有可复用性，便于创建复杂测试场景
> * 测试结果统计报告采用Allure简洁清晰，附带详尽日志记录，包括接口请求耗时、请求响应数据等
> * 具有可扩展性

### 简单的case样例：
#### 1.单一测试步骤
```
        new RunRequest("get with params")
        //定义变量值，变量值可以从自定义方法获取
                .with_variables("{'foo1': 'bar11', 'foo2': 'bar21', 'sum_v': '${sum_two(1,2)}'}")
                .get("/get")
                //http请求参数，支持自定义方法和变量
                .with_params("{'foo1': '$foo1', 'foo2': '$foo2', 'sum_v': '$sum_v'}")
                //请求header，支持自定义方法和变量
                .with_headers("{'User-Agent': 'HttpRunner/${get_httprunner_version()}'}")
                .extract()
                //请求响应体结果导出
                .with_jmespath("body.args.foo2", "foo3")
                .validate()
                //结果校验
                .assert_equal("status_code", 200)
                .assert_equal("body.args.foo1", "bar11")
                .assert_equal("body.args.sum_v", "1002")
                .assert_equal("body.args.foo2", "bar21")
```


#### 2.case嵌套：
```
new Step(
        new RunTestCase("request with functions")
            //自定义变量
            .with_variables("{'foo1': 'testcase_ref_bar1', 'expect_foo1': 'testcase_ref_bar1'}")
            //钩子函数
            .setup_hook("${sleep(0.1)}")
            //嵌套的测试方法的类，支持全量步骤调用，也支持单一步骤调用
            .call(request_with_functions_test.class)
            .teardown_hook("${sleep(0.2)}")
            //导出变量，可以输出或给后续步骤使用
            .export("['foo3']")
)

```
