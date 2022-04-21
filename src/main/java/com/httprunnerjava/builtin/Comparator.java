package com.httprunnerjava.builtin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.httprunnerjava.exception.CompareError;
import com.httprunnerjava.utils.JsonUtils;
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

    public void objectEequals(T checkalue, T expectValue) {
        if (!checkalue.equals(expectValue)) {
            logger.error("期望值是：" + checkalue + ",实际值是：" + expectValue);
            throw new CompareError("比对结果与预期不一致");
        }
    }

    public void typeMatch(T checkalue, T expectValue) {
        //TODO:未实现

    }

    public void lessThan(T checkalue, T expectValue) {
        if (checkalue instanceof Integer) {
            assert (Integer) checkalue < (Integer) expectValue;
        } else if (checkalue instanceof Double) {
            //TODO: int 和 double 有没有大于小于的比较？
            assert (Double) checkalue < (Double) expectValue;
        } else {
            throw new CompareError("比对结果与预期不一致");
        }
    }

    public void listContains(T checkalue, Object expectValue) {
        JSONArray expectValueArray;
            if (checkalue instanceof JSONArray) {
                if (expectValue instanceof String) {
                    expectValueArray = JSON.parseArray(expectValue.toString());
                    JsonUtils.containJsonArray((JSONArray) checkalue, expectValueArray, null);
                }
            } else {
                throw new CompareError("比对结果与预期不一致");
        }
    }

    public void listEmpty(T checkalue, Object expectValue) {
        if (checkalue instanceof List) {
            if (!(((List) checkalue).size() == 0)) {
                throw new CompareError("比对结果与预期不一致");
            }
        } else {
            throw new CompareError("比对结果与预期不一致");
        }
    }

    public void jsonEquals(Object checkalue, Object expectValue) {
        JsonUtils.compareJson(checkalue, expectValue);
    }

    public void notListEmpty(T checkalue, Object expectValue) {
        if (checkalue instanceof List) {
            if ((((List) checkalue).size() == 0)) {
                throw new CompareError("比对结果与预期不一致");
            }
        } else {
            throw new CompareError("比对结果与预期不一致");
        }
    }
}
