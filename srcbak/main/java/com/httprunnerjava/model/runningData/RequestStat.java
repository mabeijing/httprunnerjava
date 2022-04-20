package com.httprunnerjava.model.runningData;

import lombok.Data;

@Data
public class RequestStat {

    private Long contentSize;

    private Long responseTimeMs;

    private Float elapsedMs;
}

