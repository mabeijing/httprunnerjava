package com.httprunnerjava.model.Enum;

public enum MethodEnum {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    COPY("COPY"),
    HEAD("HEAD"),
    OPTION("OPTION");

    private final String method;

    MethodEnum(String method) {
        this.method = method;
    }

    public String getMethod(){
        return this.method;
    }

    public static MethodEnum getMethodEnum(String method){
        for (MethodEnum c : MethodEnum.values()) {
            if (c.getMethod().equals(method)) {
                return c;
            }
        }
        return null;
    }
}
