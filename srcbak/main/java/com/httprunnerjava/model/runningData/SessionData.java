package com.httprunnerjava.model.runningData;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-04-12-16:43
 * @Description:
 */
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
