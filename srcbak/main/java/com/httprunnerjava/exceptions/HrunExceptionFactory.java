package com.httprunnerjava.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HrunExceptionFactory {
    private static Logger logger = LoggerFactory.getLogger(HrunExceptionFactory.class);

    public static void create(String errorCode) {
        throw new HrunBizException(errorCode,
                ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
    }

    public static void createAndPrintError(String errorCode) {
        logger.error(ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
        throw new HrunBizException(errorCode,
                ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
    }

    public static HrunBizException getException(String errorCode) {
        return new HrunBizException(errorCode,
                ExceptionDefinitions.getExceptionDefinitions().getExceptionMessage(errorCode));
    }
}
