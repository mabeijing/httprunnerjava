package com.httprunnerjava;

import com.google.common.base.Strings;
import com.httprunnerjava.Common.Component.*;
import com.httprunnerjava.Common.Component.Enum.MethodEnum;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Model.*;
import com.httprunnerjava.Utils.CommonUtils;
import com.httprunnerjava.exceptions.ExecuteFailureException;
import com.httprunnerjava.exceptions.ParamsError;
import com.httprunnerjava.exceptions.ValidationFailureException;
import okhttp3.Response;
import com.httprunnerjava.Common.Model.RunningAttribute.StepData;
import com.httprunnerjava.Common.Model.TModel.TConfig;
import com.httprunnerjava.HrunLogger.LoggerSetting;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import lombok.Data;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.httprunnerjava.Loader.load_project_meta;
import io.qameta.allure.Allure;
import org.testng.annotations.*;

/*
与原版HttpRunner类似，HttpRunner类是测试执行的总入口，采用了testng单测框架，测试的入口方法是 new_test_start
为了和原版保持尽量一致，很多成员变量和方法同样以 __ 作为前缀，双下划线前缀 在python中是作为 private的标志，
 */

//TODO:统一处理所有方法和变量，都改成驼峰式

@Data
public class HttpRunner {

    //日志输出，项目内部用的是logback，所有的日志打印都符合slf4j的门面标准，可以将logback exclude掉，使用自己喜欢的日志框架
    private static Logger logger = LoggerFactory.getLogger(HttpRunner.class);

    //用例集（testcase）层级的参数化配置，其中可以包含 base_url，case层级的参数变量，导出字段等
    private Config config;

    //用例集的步骤，步骤可以是单个step，也可以嵌套其他case
    private List<Step> teststeps;

    //标注测试用例执行结果
    private Boolean success = false;

    //为什么有两个config？在原版python实现中，__config是HttpRunner内部访问的，config是子类访问的
    //因为Python没有private这种区分，所以采用了 __config 和 config 两个变量
    //为了保持了原版的一致，沿袭了这种用法，其中config是public的，__config是private，其实是同一个对象
    //子类覆写中，只需要写 config 和 teststeps 这两个变量。
    private TConfig __config;

    //参照上面对config的解释
    private List<TStep> __teststeps;

    //测试case集的属性数据
    private ProjectMeta project_meta = null;

    private String case_id;

    //导出字段
    private Export export;

    //case中的每一步step，都会返回对应的数据，包括success，step的name，状态值等，统一存在该字段中
    private List<StepData> step_datas = new ArrayList<>();

    //会话上下文信息，可以用来保存cookie等，更高级的用法正在探索中
    private HttpSession session;

    //上下文信息中产生的变量，比如第一个接口中导出的A字段作为某个变量，可以在第二个接口请求中传入
    private Variables session_variables = new Variables();

    // time
    private long start_at;
    private long end_at;
    private long duration;

    // log path
    private String log_path;


    private Boolean use_allure = true;

    //这个字段是原版没有的，增加的原因是原版的用例执行，完全是在httprunner内部，而java的实现版本，中间跨越了testng框架
    //为了把中间的变量串联起来，因此增加了这个字段
    private Variables extracted_variables;

    //TODO：这里只是很简单的用了一个随机值表示hashcode，需要探索下更好的实现方式，当前的目的仅仅是为了让同一个HttpRunner对象的hashcode值一致
    //重写hashcode的目的，是因为testng中，会把当前的HttpRunner对象放进一个HashMap中，map的key是HttpRunner，Value是Collection<ITestNGMethod>
    //map的key，实际存储的是HttpRunner对象的hashcode，在case执行过程中，由于HttpRunner类对象变化导致hashcode变化，用例执行完时，根据原有的hashcode找不到value了
    //testng认为此时测试用例的执行方法为空，抛出异常 java.lang.AssertionError: l should not be null
    private Integer hashCode = new Random().nextInt(999999);


    /**
     * 初始化__config和__teststeps对象
     */
    public void __init_tests__() {
        this.__config = this.getConfig().perform();
        this.__teststeps = this.getTeststeps().stream()
                .map(Step::perform)
                .collect(Collectors.toList());

        //TODO: 指定log日志级别？
        LoggerSetting.setLogLevel("DEBUG");
    }


    /*
     * 这个方法主要是由 step 中嵌套了其他testcase时执行
     * 原版中有如下内容：
     *  Examples:
     *      >>> testcase_obj = TestCase(config=TConfig(...), teststeps=[TStep(...)])
     *      >>> HttpRunner().with_project_meta(project_meta).run_testcase(testcase_obj)
     * 貌似可以实现手动调用testcase，需要的可以自行研究下是否满足
     * @param testcase
     */
    public HttpRunner runInlineTestcase(TestCase testcase) {

        new_run_testcase(testcase);
        for (TStep step : this.__teststeps) {
            testStart(step, null);
        }

        session_variables.update(extracted_variables);
        duration = System.currentTimeMillis() - start_at;

        return this;
    }

    public void parseConfig(TConfig config) {
        config.getVariables().update(session_variables);
        config.setVariables(Parse.parse_variables_mapping(
                config.getVariables(), project_meta.getFunctions()
        ));
        config.setName(
                Parse.parse_data(config.getName(), config.getVariables(), project_meta.getFunctions())
        );
        config.setBase_url(
                Parse.parse_data(config.getBase_url(), config.getVariables(), project_meta.getFunctions())
        );
    }


    /**
     * 执行step，step类型可能是某个request，也可能是嵌套的其他testcase
     */
    public Map<String,Object> runStep(TStep step) {
        logger.info("run step begin: {} >>>>>>", step.getName());
        StepData step_data = null;

        if (step.getRequest() != null) {
            step_data = this.__run_step_request(step);
        } else if (step.getTestcase() != null) {
            step_data = this.__run_step_testcase(step);
        } else {
            logger.error(step.toString());
            throw new ParamsError(String.format("teststep is neither a request nor a referenced testcase: %s",step));
        }

        step_datas.add(step_data);
        logger.info("run step end: {} <<<<<<\n", step.getName());
        return step_data.getExport_vars();
    }

    public StepData __run_step_request(TStep step) {
        ResponseObject.setCurrentRespBody(null);
        StepData step_data = new StepData(step.getName());

        //TODO: 低优先级 deal upload request
        // prepare_upload_step(step,this.__project_meta.getFuntions());
        // request_dict.remove("upload");

        Optional.of(step.getSetup_hooks()).ifPresent(setupHooks->
                __call_hooks(setupHooks, step.getVariables(), "setup request")
        );

        TRequest parsed_request_dict = (TRequest) Parse.parse_data(
                step.getRequest(), step.getVariables(), project_meta.getFunctions()
        );

        parsed_request_dict.getHeaders().setdefault("HRUN-Request-ID",
                String.format("HRUN-%s-%s", case_id, System.currentTimeMillis()));
        step.getVariables().update("request", parsed_request_dict);

        // prepare arguments
        MethodEnum method = parsed_request_dict.getMethod();
        String url_path = parsed_request_dict.getUrl();

        String url = CommonUtils.build_url(this.__config.getBase_url().getRaw_value(), url_path);
        parsed_request_dict.setVerify(this.__config.getVerify());

        ResponseObject resp_obj = null;
        try{
            Response resp = session.request(method, url, parsed_request_dict);
            resp_obj = new ResponseObject(resp);
        }catch (Exception e){
            throw new ExecuteFailureException("http接口执行失败。");
        }

        step.getVariables().update("response", resp_obj);

        Optional.of(step.getTeardown_hooks()).ifPresent(tearDownHooks->
                __call_hooks(tearDownHooks, step.getVariables(), "setup request")
        );

        HashMap<String, String> extractors = step.getExtract();
        Variables extract_mapping = resp_obj.extract(extractors, step.getVariables(), project_meta.getFunctions());
        step_data.setExport_vars(extract_mapping.translateToMap());

        Variables variables_mapping = step.getVariables();
        variables_mapping.update((extract_mapping));

        List<Validator> validators = step.getValidators();
        boolean session_success = false;
        try {
            resp_obj.validate(validators, variables_mapping, project_meta.getFunctions());
            session_success = true;
        } catch (ValidationFailureException e) {
            log_req_resp_details(url, method, parsed_request_dict, resp_obj);
            duration = System.currentTimeMillis() - start_at;
            //TODO 原版这里抛出了异常，然后整个case就不再执行了，想了想还是先不抛出异常了，至于结果如何展示，后面再考虑下
            throw e;
        } finally {
            this.success = session_success;
            step_data.setSuccess(session_success);
            //TODO:            if hasattr(self.__session, "data"):
            //                # httprunner.client.HttpSession, not locust.clients.HttpSession
            //                # save request & response meta data
            //                self.__session.data.success = session_success
            //                self.__session.data.validators = resp_obj.validation_results
            //                # save step data
            //                step_data.data = self.__session.data
        }

        return step_data;
    }

    public void log_req_resp_details(String url,MethodEnum method,TRequest request,ResponseObject response){

        //log request
        String err_msg = "\n**************** 完整的请求和相应信息 ****************\n" +
                "**************** DETAILED REQUEST & RESPONSE ****************\n" +
                "\n====== request details ======\n" +
                "url: " + url + "\n" +
                "method: " + method.getMethod() + "\n" +
                request.log_detail() +
                "\n====== response details ======\n" +
                response.log_detail();

        logger.error(err_msg);
    }

    /**
     * run teststep: referenced testcase
     */
    public StepData __run_step_testcase(TStep step) {
        StepData step_data = new StepData(step.getName());
        Variables step_variables = step.getVariables();
        Export step_export = step.getExport();

        // setup hooks
        Optional.of(step.getSetup_hooks()).ifPresent(setupHooks->
                __call_hooks(setupHooks, step_variables, "setup testcase")
        );

        HttpRunner case_result = null;
        try {
            if (step.getTestcase().getDeclaredField("config") != null
                    && step.getTestcase().getDeclaredField("teststeps") != null) {
                Class<? extends HttpRunner> testcase_cls = step.getTestcase();

                case_result = testcase_cls
                        .newInstance()
                        .with_session(session)
                        .with_case_id(case_id)
                        .with_variables(step_variables)
                        .with_export(step_export)
                        .run();
            } else {
                logger.error("嵌套的testcase类中未包含config和teststep成员变量，或两者两边为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("暂时不支持HttpRunner类以外的case嵌套");
            //TODO： step.tostring方法需要添加一下
            throw new ParamsError("Invalid teststep referenced testcase" + step);
        }

        Optional.of(step.getTeardown_hooks()).ifPresent(tearDownHooks->
                __call_hooks(tearDownHooks, step_variables, "teardown testcase")
        );

        step_data.setTestCasedata(case_result.getStep_datas());
        step_data.setExport_vars(case_result.get_export_variables().translateToMap());
        step_data.setSuccess(case_result.getSuccess());
        this.success = case_result.success;

        if (step_data.getExport_vars() != null && step_data.getExport_vars().size() != 0)
            logger.info("export variables: {}", step_data.getExport_vars());

        return step_data;
    }

    public void __call_hooks(Hooks hooks, Variables step_variables, String hook_msg) {
        logger.info("call hook actions: {}", hook_msg);
        for (Hooks.HookString hook : hooks.getContent()) {
            if (hook.getType() == 1) {
                // format 1: ["${func()}"]
                logger.debug("call hook function: {}", hook.getFuncHook());
                Parse.parse_data(hook.getFuncHook(), step_variables, project_meta.getFunctions());
            } else if(hook.getType() == 2 && hook.getMapHook().size() == 1) {
                // format 2: {"var": "${func()}"}
                Map.Entry<LazyString,LazyString> entry = hook.getMapHook().entrySet().iterator().next();
                Object hook_content_eval = Parse.parse_data(
                        entry.getValue(), step_variables, project_meta.getFunctions()
                );
                logger.debug(
                        "call hook function: {}, got value: {}",
                        entry.getValue().getRaw_value(),
                        entry.getValue().getEvalString()
                );
                logger.debug(
                        "assign variable: {} = {}",
                        entry.getKey().getRaw_value(),
                        entry.getValue().getEvalString()
                );
                step_variables.setVariables(
                        entry.getKey().getRaw_value(),entry.getValue().getEvalValue()
                );
            }else{
                logger.error("Invalid hook format: {}", hook);
            }
        }
    }

    public HttpRunner run() {
        __init_tests__();
        TestCase testcase_obj = new TestCase(__config, __teststeps);
        return runInlineTestcase(testcase_obj);
    }

    public HttpRunner with_session(HttpSession session) {
        this.session = session;
        return this;
    }

    public HttpRunner with_case_id(String case_id) {
        this.case_id = case_id;
        return this;
    }

    public HttpRunner with_variables(Variables variables) {
        session_variables = variables;
        return this;
    }

    public HttpRunner with_export(Export export) {
        this.export = export;
        return this;
    }

    public Variables get_export_variables() {
        Export export_var_names;
        if (export == null || export.getContent().size() == 0)
            export_var_names = __config.getExport();
        else
            export_var_names = export;

        Variables export_vars_mapping = new Variables();
        for (String var_name : export_var_names.getContent()) {
            if (!session_variables.getContent().containsKey(var_name))
                HrunExceptionFactory.create("E0069");

            export_vars_mapping.getContent().put(var_name, session_variables.get(var_name));
        }

        return export_vars_mapping;
    }

    /*
    用例执行前执行该方法，加载变量和project数据
     */
    @BeforeClass
    public void before_test_start() {
        this.__init_tests__();
        project_meta = Optional.ofNullable(project_meta).orElseGet( () ->
                load_project_meta(
                        Optional.ofNullable(this.__config.getPath())
                                .orElseGet(() -> {
                                    logger.info("config中未指定debugtalk文件位置，默认优先获取测试执行类所在目录，其次取HttpRunner所在目录下的默认Debugtal文件");
                                    return new LazyString(this.getClass().getPackage().getName());
                                }))
        );

        if (Strings.isNullOrEmpty(case_id))
            case_id = UUID.randomUUID().toString();

        Variables config_variables = this.__config.getVariables();
        config_variables.update(session_variables);

        __config.setName(Parse.parse_data(
                __config.getName(), config_variables, project_meta.getFunctions()
        ));

        //TODO:下面这两行应该可以暂时不要，后面再调整一下
//        Allure.getLifecycle().updateTestCase(result -> result.setName(__config.getName().getEvalString()));
//        Allure.description(String.format("TestCase ID: %s", __case_id));

        logger.info(
                String.format("Start to run testcase: %s, TestCase ID: %s", this.__config.getName().getEvalString(),
                        this.getCase_id())
        );

        new_run_testcase(new TestCase(this.__config, this.__teststeps));

        extracted_variables = new Variables();
    }

    public void new_run_testcase(TestCase testcase) {
        this.__config = testcase.getConfig();
        this.__teststeps = testcase.getTeststeps();
        project_meta = Optional.ofNullable(project_meta).orElseGet( () ->
                load_project_meta(
                        Optional.ofNullable(this.__config.getPath())
                                .orElseGet(() -> {
                                    logger.info("config中未指定debugtalk文件位置，默认优先获取测试执行类所在目录，其次取HttpRunner所在目录下的默认Debugtal文件");
                                    return new LazyString(this.getClass().getPackage().getName());
                                }))
        );

        this.parseConfig(this.__config);
        start_at = System.currentTimeMillis();
        step_datas = new ArrayList<>();

        try {
            if(session == null)
                session = new HttpSession(this);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("创建HttpSession对象失败");
        }

        extracted_variables = new Variables();
    }

    @DataProvider(name = "HrunDataProvider")
    public Iterator<Object[]> createData() {
        List<Object[]> users = new ArrayList<>();
        for (TStep u : this.__teststeps) {
            users.add(new TStep[]{u, null});
        }
        return users.iterator();
    }

    @Test(dataProvider = "HrunDataProvider")
    public void testStart(TStep step, Map<String, Object> params) {
        Variables config_variables = this.__config.getVariables();
        if (params != null && !params.isEmpty()) {
            config_variables.update(params);
        }

        step.setVariables(CommonUtils.merge_variables(step.getVariables(), extracted_variables));
        step.setVariables(CommonUtils.merge_variables(step.getVariables(), this.__config.getVariables()));

        step.setVariables(Parse.parse_variables_mapping(step.getVariables(), project_meta.getFunctions()));

        Map<String,Object> extract_mapping = null;
        try {
            if(this.getUse_allure()) {
                Allure.step(String.format("step: %s", step.getName()));
            }
            extract_mapping = this.runStep(step);
        } catch ( ValidationFailureException e) {
            throw e;
        }

        extracted_variables.update(extract_mapping);
    }

    @AfterClass
    public void after_run_testcase() {
        session_variables.update(extracted_variables);
        duration = System.currentTimeMillis() - start_at;

        logger.info("TODO：generate testcase log: {self.__log_path}");
    }

    public void test() {
        System.out.println("this is test method");
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public static HttpRunner manual_execute_single_test_start(Class<? extends HttpRunner> cls, Integer index, Map<String, Object> params){
        try{
            HttpRunner httprunner = cls.newInstance();
            httprunner.setUse_allure(false);
            httprunner.before_test_start();
            httprunner.testStart(httprunner.getTeststeps().get(index).perform(), params);
            return httprunner;
        }catch (Exception e) {
            logger.error("手动执行case失败");
            e.printStackTrace();
        }

        return null;
    }

    // 手动执行teststep的入口
    // 手动执行所有的步骤
    public static HttpRunner manual_execute_all_test_start(Class<? extends HttpRunner> cls, Integer index, Map<String, Object> params){
        try{
            HttpRunner httprunner = cls.newInstance();
            httprunner.setUse_allure(false);
            httprunner.manual_test_start(params);
            return httprunner;
        }catch (Exception e) {
            logger.error("手动执行case失败");
            e.printStackTrace();
        }

        return null;
    }

    public void manual_test_start(Map<String,Object> param){
        this.__init_tests__();

        if( this.project_meta == null ){
            project_meta = Optional.ofNullable(project_meta).orElseGet( () ->
                    load_project_meta(
                            Optional.ofNullable(this.__config.getPath())
                                    .orElseGet(() -> {
                                        logger.info("config中未指定debugtalk文件位置，默认优先获取测试执行类所在目录，其次取HttpRunner所在目录下的默认Debugtal文件");
                                        return new LazyString(this.getClass().getPackage().getName());
                                    }))
            );
        }

        if(Strings.isNullOrEmpty(case_id))
            this.case_id = UUID.randomUUID().toString();

        Variables config_variables = this.__config.getVariables();
        if(param != null && !param.isEmpty()){
            config_variables.update(param);
        }
        config_variables.update(this.session_variables);
        __config.setName(Parse.parse_data(
                __config.getName(), config_variables, project_meta.functions
        ));


        Allure.getLifecycle().updateTestCase(result -> result.setName(__config.getName().getEvalString()));
        Allure.description(String.format("TestCase ID: %s", case_id));

        logger.info(
                String.format("Start to run testcase: %s, TestCase ID: %s",this.__config.getName().getEvalString(),this.getCase_id())
        );

        try{
            manual_run_testcase(
                    new TestCase(this.__config, this.__teststeps)
            );
        }catch(Exception e){
            if(this.getConfig().getCatchAllExpection()){
                logger.error("执行过程中捕获到异常，但不影响后续执行，错误信息如下");
                e.printStackTrace();
            }
        } finally {
            logger.info("TODO：generate testcase log: {self.__log_path}");
        }
    }

    public void manual_run_testcase(TestCase testcase){
        this.__config = testcase.getConfig();
        this.__teststeps = testcase.getTeststeps();
        if( this.project_meta == null )
            project_meta = Optional.ofNullable(project_meta).orElseGet( () ->
                    load_project_meta(
                            Optional.ofNullable(this.__config.getPath())
                                    .orElseGet(() -> {
                                        logger.info("config中未指定debugtalk文件位置，默认优先获取测试执行类所在目录，其次取HttpRunner所在目录下的默认Debugtal文件");
                                        return new LazyString(this.getClass().getPackage().getName());
                                    }))
            );
//            this.project_meta = load_project_meta(this.__config.getPath());

        this.parseConfig(this.__config);
        this.start_at = System.currentTimeMillis();
        this.step_datas = new ArrayList<StepData>();

        try {
            this.session = Optional.ofNullable(session)
                    .orElse(new HttpSession(this));
        } catch (Exception e) {
            logger.error("创建HttpSession对象失败");
        }

        Variables extracted_variables = new Variables();

        for(TStep step : this.__teststeps){
            step.setVariables(CommonUtils.merge_variables(step.getVariables(), extracted_variables));
            step.setVariables(CommonUtils.merge_variables(step.getVariables(), this.__config.getVariables()));

            step.setVariables(Parse.parse_variables_mapping(step.getVariables(), this.project_meta.getFunctions()));

            Map<String,Object> extract_mapping = null;
            try {
                Allure.step(String.format("step: %s", step.getName()));
                extract_mapping = this.runStep(step);
            }catch(Exception e){
                e.printStackTrace();
            }

            extracted_variables.update(extract_mapping);
        }

        this.session_variables.update(extracted_variables);
        this.duration = System.currentTimeMillis() - this.start_at;
    }
}
