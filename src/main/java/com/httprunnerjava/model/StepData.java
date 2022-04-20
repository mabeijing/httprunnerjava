package com.httprunnerjava.model;

import com.httprunnerjava.model.runningData.SessionData;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: ChuCan
 * @CreatedDate: 2022-04-07-1:44
 * @Description:
 */
@Data
public class StepData {

    private boolean success;
    private String name;
    private List<SessionData> dataList;
    private SessionData data;
    private List<StepData> testCasedata;
    private Map<String,Object> exportVars;

    public StepData(String name){
        this.name = name;
    }


}
