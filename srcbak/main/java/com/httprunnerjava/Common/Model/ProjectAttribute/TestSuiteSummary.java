package com.httprunnerjava.Common.Model.ProjectAttribute;

import com.httprunnerjava.Common.Model.RunningAttribute.PlatformInfo;
import com.httprunnerjava.Common.Model.RunningAttribute.TestCaseSummary;
import com.httprunnerjava.Common.Model.TestCaseAttribute.TestCaseTime;

import java.util.List;


public class TestSuiteSummary {
    private Boolean success;
    private Stat stat;
    private TestCaseTime time;
    private PlatformInfo platform;
    private List<TestCaseSummary> testcases;




}
