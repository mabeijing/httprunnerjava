package com.httprunnerjava.model.runningData;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SessionData {
    private Boolean success;
    private List<ReqRespData> reqResps;
    private RequestStat stat;
    private AddressData address;

    public SessionData(){
        success = false;
        reqResps = new ArrayList<>();
        stat = new RequestStat();
        address = new AddressData();
    }
}
