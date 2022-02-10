package com.httprunnerjava.Common.Model.RunningAttribute;

import lombok.Data;

@Data
public class RequestStat {

    private Float content_size;

    private Float response_time_ms;

    private Float elapsed_ms;
}
