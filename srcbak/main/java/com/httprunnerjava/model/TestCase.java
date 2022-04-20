package com.httprunnerjava.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
