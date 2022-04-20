package com.httprunnerjava.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class HrunExceptionFactory {

    public static void create(String errorCode) {
        throw new HrunBizException(errorCode,
                ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
    }

    public static void createAndPrintError(String errorCode) {
        log.error(ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
        throw new HrunBizException(errorCode,
                ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
    }

    public static HrunBizException getException(String errorCode) {
        return new HrunBizException(errorCode,
                ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
    }
}
