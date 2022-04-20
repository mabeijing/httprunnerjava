package com.httprunnerjava.Common.Model;

import com.httprunnerjava.Common.Model.TModel.TConfig;
import com.httprunnerjava.Common.Component.TStep;
import lombok.Data;

import java.util.List;

@Data
public class TestCase {

    private TConfig config;
    private List<TStep> teststeps;

    public TestCase(TConfig config, List<TStep> teststeps){
        this.config = config;
        this.teststeps = teststeps;
    }



}
