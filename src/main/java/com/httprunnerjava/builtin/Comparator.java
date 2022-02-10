package com.httprunnerjava.builtin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.httprunnerjava.Utils.JsonUtils;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//caution！！！
// 所有比对方法抛出的HrunBizException类异常，都会被上层的validate方法捕获，这里的异常抛出，只是为了表明是某个方法的某类错误，不会被上层逻辑发现。
public class Comparator<T> {
    static Logger logger = LoggerFactory.getLogger(Comparator.class);

    private final Class<?> cls;

    public Comparator(T t1) {
        cls = t1.getClass();
    }

    public void equals(T check_value, T expect_value) {
        if (!check_value.equals(expect_value)) {
            logger.error("期望值是：" + check_value + ",实际值是：" + expect_value);
            HrunExceptionFactory.createAndPrintError("E0064");
        }
    }

    public void type_match(T check_value, T expect_value) {
        //TODO:未实现

    }

    public void less_than(T check_value, T expect_value) {
        if (check_value instanceof Integer) {
            assert (Integer) check_value < (Integer) expect_value;
        } else if (check_value instanceof Double) {
            //TODO: int 和 double 有没有大于小于的比较？
            assert (Double) check_value < (Double) expect_value;
        } else {
            HrunExceptionFactory.createAndPrintError("E0023");
        }
    }

    public void list_contains(T check_value, Object expect_value) {
        JSONArray expect_value_array;
        if (check_value instanceof JSONArray) {
            if (expect_value instanceof String) {
                expect_value_array = JSON.parseArray(expect_value.toString());
                JsonUtils.containJsonArray((JSONArray) check_value, expect_value_array, null);
            }
        } else {
            HrunExceptionFactory.createAndPrintError("E0023");
        }
    }

    public void list_empty(T check_value, Object expect_value) {
        if (check_value instanceof List) {
            if (!(((List) check_value).size() == 0)) {
                HrunExceptionFactory.createAndPrintError("E0072");
            }
        } else {
            HrunExceptionFactory.createAndPrintError("E0066");
        }
    }

    public void not_list_empty(T check_value, Object expect_value) {
        if (check_value instanceof List) {
            if ((((List) check_value).size() == 0)) {
                HrunExceptionFactory.createAndPrintError("E0072");
            }
        } else {
            HrunExceptionFactory.createAndPrintError("E0066");
        }
    }
}
