package com.httprunnerjava.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HrunBizException extends RuntimeException {
    public static final String DEFAULT_FAULT_CODE = "-1";
    private String code;
    private String message;

    private static final long serialVersionUID = -8794392106020941171L;

    public HrunBizException() {
    }

    public HrunBizException(String message, Throwable cause) {
        super(message, cause);
    }

    public HrunBizException(String code, String message) {
        this(code, message, new Throwable());
    }

    public HrunBizException(String code, String message, String internalMessage) {
        this(code, message, internalMessage, null);
    }

    public HrunBizException(String code, String message, Throwable throwable) {
        this(code, message, throwable.getMessage(), throwable);
    }

    public HrunBizException(String code, String message, String internalMessage,
                           Throwable throwable) {
        super("[" + code + "] - [" + message + "]" + internalMessage, throwable);
        this.message = message;
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取异常的调用堆栈信息。
     *
     * @return 调用堆栈
     */
    public static String toStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        try {
            e.printStackTrace(pw);
            return sw.toString();
        } catch(Exception e1) {
            return "";
        }
    }
}
