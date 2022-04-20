package com.httprunnerjava.model.runningData;

import lombok.Data;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-12-17:03
 * @Description:
 */
@Data
public class RequestStat {

    private Long contentSize;

    private Long responseTimeMs;

    private Float elapsedMs;
}

