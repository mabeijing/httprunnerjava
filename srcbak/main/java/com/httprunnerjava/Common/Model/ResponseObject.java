package com.httprunnerjava.Common.Model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Component.Validator;
import com.httprunnerjava.Common.Component.ValidatorDict;
import com.httprunnerjava.Common.Component.Variables;
import com.httprunnerjava.Parse;
import com.httprunnerjava.Utils.JsonUtils;
import com.httprunnerjava.builtin.Comparator;
import com.httprunnerjava.exceptions.*;
import lombok.Data;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Data
public class ResponseObject {

    private static String currentRespBody;

    private Integer status_code;
    //TODO：下面两个对象需要做成jsonObjec形式
    private Object headers;
    private Object cookies;
    private JSONObject body;
    private Object srcBody;

    static Logger logger = LoggerFactory.getLogger(ResponseObject.class);

    private Response src_resp_obj;

    private String resp_obj_strEntity;

    private List<ValidatorDict> validation_results = new ArrayList<>();

    public ResponseObject(Response src_resp_obj) {
        this.src_resp_obj = src_resp_obj;
        this.status_code = src_resp_obj.code();
        this.headers = src_resp_obj.headers();
        this.cookies = src_resp_obj.header("cookie");
        this.body = JSONObject.parseObject(ResponseObject.getCurrentRespBody());
    }

    public void validate(List<Validator> validators,
                         Variables variables,
                         Class<?> functions_mapping){
        if(validators == null || validators.size() == 0){
            return ;
        }
        boolean validate_pass = true;
        List<String> failures = new ArrayList<>();
        for(Validator each : validators) {
            String check_item = each.getCheck_item();
            Object check_item_valued;
            //TODO: 低优先级 这里可能会报错，还没有测试过
            if (check_item.contains("$")) {
                check_item_valued = Parse.parse_data(new LazyString(check_item), variables, functions_mapping).getEvalValue();
            }

            check_item_valued = _search_jmespath(check_item);

            String assert_method = each.getComparator();

            Method assert_func = null;
            try {
                assert_func = Parse.get_mapping_function(assert_method, functions_mapping);
            }catch (Exception e) {
                logger.error(String.valueOf(e.getStackTrace()));
            }

            // expect item
            Object expect_value;
            Object expect_item = each.getExpect_value();
            if(expect_item instanceof String){
                expect_value = Parse.parse_data(new LazyString((String)expect_item), variables, functions_mapping).getEvalValue();
            }else
                expect_value = expect_item;
            // message
//                String message = each.getMessage();
            //TODO: 低优先级 parse message with config/teststep/extracted variables
//                        message = parse_data(message, variables_mapping, functions_mapping)
//                validate_msg = f"assert {check_item} {assert_method} {expect_value}({type(expect_value).__name__})"
//                validator_dict = {
//                        "comparator": assert_method,
//                        "check": check_item,
//                        "check_value": check_value,
//                        "expect": expect_item,
//                        "expect_value": expect_value,
//                        "message": message,
//            }

            //TODO:日志需要优化
            String validate_msg = String.format("\nassert %s %s %s(type is %s)",
                    check_item,
                    assert_method,
                    expect_value,
                    expect_value == null ? "null" : expect_value.getClass());
            try{
                if(check_item_valued == null && (String.valueOf(expect_value).equals("NULL")
                        || String.valueOf(expect_value).equals("None"))){
                    continue;
                }
                else {
                    Comparator<?> comparator = new Comparator(check_item_valued);
                    assert_func.invoke(comparator, check_item_valued, expect_value);
                }
                validate_msg += "\t==> pass";
                logger.info(validate_msg);
            }catch (Exception e) {
                validate_pass = false;
                if (e instanceof InvocationTargetException) {
                    Throwable targetEx =((InvocationTargetException)e).getTargetException();
                    if(targetEx instanceof HrunBizException){
                        validate_msg += "\t==> fail \n";
                        validate_msg += String.format("check_item : %s \n",check_item);
                        validate_msg += String.format("check_value : %s \n",check_item_valued);
                        validate_msg += String.format("assert_method : %s \n",assert_method);
                        validate_msg += String.format("expect_value : %s \n",expect_value);
                        logger.error(validate_msg);
                        failures.add(validate_msg);
                    }else{
                        //如果捕获的异常不属于HrunBizException，则是反射方法执行时出错，需要输出详细的错误信息
                        logger.error(e.toString());
                        logger.error("work exception" + ExcpUtil.getStackTraceString(e));
                    }
                } else {
                    logger.error(e.toString());
                    logger.error("work exception" + ExcpUtil.getStackTraceString(e));
                }
            }
        }

        //TODO:异常抛出需要优化，failures目前还是空的
        if(!validate_pass){
            throw new ValidationFailureException(
                    String.join("\n", failures.toString())
            );
        }
    }

    public Object _search_jmespath(String expr){
        JSONObject resp_obj_meta = new JSONObject();
        resp_obj_meta.put("status_code", status_code);
        resp_obj_meta.put("headers", headers);
        resp_obj_meta.put("cookies", cookies);
        resp_obj_meta.put("body",body);

        return _search(expr, resp_obj_meta);
    }

    public Object _search(String expr, JSON data){
        String tempExpr = expr;

        try{
            String[] strArray = tempExpr.split("\\.");
            if(tempExpr.length() == 0)
                return "";

            if(strArray.length == 1){
                if(strArray[0].matches("\\d+")){
                    return JsonUtils.getByNumKey(data,strArray[0]);
                }
                else {
                    return ((JSONObject)data).get(strArray[0]);
                }
            }else{
                if(strArray[0].matches("\\d+")) {
                    data = (JSON)JsonUtils.getByNumKey(data,strArray[0]);
                    expr = expr.substring(strArray[0].length() + 1);
                    return _search(expr, data);
                }else{
                    data = JsonUtils.getSubJson(data,strArray[0]);
                    expr = expr.substring(strArray[0].length()+1);
                    return _search(expr, data);
                }
            }
        }catch(Exception e){
            logger.error(String.valueOf(e.getStackTrace()));
            throw new ExtractParamError("需要导出的变量未找到，变量的路径为" + expr);
        }

    }

    public Variables extract(HashMap<String,String> extractors, Variables variables_mapping, Class<?> functions_mapping) {
        if(extractors == null || extractors.size() == 0)
            return new Variables();

        HashMap<String,Object> extract_mapping = new HashMap<>();
        for(Map.Entry<String,String> each : extractors.entrySet()){
            String str = new LazyString(each.getValue()).to_value(variables_mapping, functions_mapping).getEvalString();
            Object field_value = _search_jmespath(str);
            if(field_value == null) {
                logger.error("reponse中的" + str + "结果为null，需确认！！！");
            }
            extract_mapping.put(each.getKey(),field_value);
        }

        logger.info("extract mapping: {}", extract_mapping);
        return new Variables(extract_mapping);
    }

    public static void setCurrentRespBody(String body){
        currentRespBody = body;
    }

    public static String getCurrentRespBody(){
        return currentRespBody;
    }

    public String log_detail(){
        return "\n" + "headers: " + Optional.ofNullable(status_code).map(Object::toString).orElse("NULL") + "\n" +
                "params: " + Optional.ofNullable(headers).map(Object::toString).orElse("NULL") + "\n" +
                "req_json: " + Optional.ofNullable(body).map(JSON::toString).orElse("NULL") + "\n";

    }
}
