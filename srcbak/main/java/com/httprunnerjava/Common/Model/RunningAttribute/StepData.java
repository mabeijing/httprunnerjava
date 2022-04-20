package com.httprunnerjava.Common.Model.RunningAttribute;

import com.httprunnerjava.Common.Component.Variables;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StepData {

    private boolean success;
    private String name;
    private List<SessionData> dataList;
    private SessionData data;
    private List<StepData> testCasedata;
    private Map<String,Object> export_vars;

    public StepData(String name){
        this.name = name;
    }


}
