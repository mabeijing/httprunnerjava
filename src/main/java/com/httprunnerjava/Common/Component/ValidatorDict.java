package com.httprunnerjava.Common.Component;

public class ValidatorDict {
    //TODO：这里可以分析一下，check_value和expect_value到底有哪些类型
    // string num(int float) boolean 还有其他的吗？
    private String comparator;
    private String check;
    private Object check_value;
    private String expect;
    private Object expect_value;
    private String message;
    private String check_result;
}
