package com.httprunnerjava.Common.Component.Intf;

import com.httprunnerjava.Common.Component.Variables;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public interface ParseableIntf {
    //这个接口是可解析约定接口，如果继承该接口，表示内部可调用parse方法和to_value方法
//    void parse(Set check_variables_set);
//    ParseableIntf parse(Variables variables_mapping);
    ParseableIntf to_value(Variables variables_mapping,Class functions_mapping);
}
