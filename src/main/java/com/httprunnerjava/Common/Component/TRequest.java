package com.httprunnerjava.Common.Component;

import com.alibaba.fastjson.JSON;
import com.httprunnerjava.Common.Component.Enum.MethodEnum;
import com.httprunnerjava.Common.Component.Intf.ParseableIntf;
import com.httprunnerjava.Common.Component.LazyContent.LazyContent;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import lombok.Data;

import java.util.*;


@Data
public class TRequest implements ParseableIntf {
    private MethodEnum method;
    private String url;
    private Params params;
    private Headers headers;
    private ReqJson req_json;
    private LazyString data;
    //TODO:
    // private Cookies cookies;
    private Float timeout;
    private boolean allow_redirects;
    private boolean verify;
    private Object upload; //TODO：上传文件

    List<ParseableIntf> needParseMember = new ArrayList<>();

    public TRequest(String method, String url){
        this.method = MethodEnum.getMethodEnum(method);
        this.url = url;
        this.headers = new Headers();
        this.params = new Params();
        needParseMember.addAll(new ArrayList<>(Arrays.asList(this.headers, this.params)));
    }

    public void setData(LazyString data){
        this.data = data;
        this.needParseMember.add(this.data);
    }

    public void setReq_json(ReqJson req_json){
        this.req_json = req_json;
        this.needParseMember.add(this.req_json);
    }

    public TRequest(){
        needParseMember.addAll(Arrays.asList(this.headers, this.params));
    }

//    @Override
//    public void parse(Set check_variables_set) {
//
//    }
//
//    @Override
//    public ParseableIntf parse(Variables variables_mapping) {
//        this.needParseMember.stream().forEach( e -> {
//            e.parse(variables_mapping);
//        });
//        return this;
//    }

    @Override
    public ParseableIntf to_value(Variables variables_mapping, Class functions_mapping) {
        this.needParseMember.forEach(e ->
            Optional.ofNullable(e).orElse(new LazyString("")).to_value(variables_mapping, functions_mapping)
        );
        return this;
    }

    public String log_detail(){
        return "\n" + "headers: " + Optional.ofNullable(headers).map(Headers::toString).orElse("NULL") + "\n" +
                "params: " + Optional.ofNullable(params).map(Params::toString).orElse("NULL") + "\n" +
                "req_json: " + Optional.ofNullable(req_json).map(ReqJson::toString).orElse("NULL") + "\n" +
                "data: " + Optional.ofNullable(data).map(LazyContent::toString).orElse("NULL") + "\n" +
                "timeout: " + Optional.ofNullable(timeout).map(Object::toString).orElse("NULL") + "\n";
    }

}
