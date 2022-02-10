package com.httprunnerjava.Common.Model.RunningAttribute;

import com.httprunnerjava.Common.Component.Validators;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SessionData {
    private Boolean success;
    private List<ReqRespData> req_resps;
    private RequestStat stat;
    private AddressData address;
    private Validators validators;

    public SessionData(){
        success = false;
        req_resps = new ArrayList<>();
        stat = new RequestStat();
        address = new AddressData();
        validators = new Validators();
    }
}
