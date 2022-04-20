package com.httprunnerjava.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-08-23:29
 * @Description:
 */
@Data
@NoArgsConstructor
public class TestCase {

    private Config config;

    private List<Step> teststeps;

    public TestCase(Config config, List<Step> teststeps) {
        this.config = config;
        this.teststeps = teststeps;
    }

}
