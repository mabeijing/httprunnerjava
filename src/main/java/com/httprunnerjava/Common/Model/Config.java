package com.httprunnerjava.Common.Model;

import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Component.*;
import com.httprunnerjava.Common.Model.TModel.TConfig;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Data
public class Config {
    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private LazyString __name;

    private Boolean __verify;

    private LazyString __base_url;

    private Variables __variables;

    private Parameters parameters;

    private Export __export;

    private LazyString __path;

    private Integer __weight;

    private Boolean isDebug = false;

    private Boolean catchAllExpection = false;

    private Integer timeOut;

    public Config(String name) {
        this.__name = new LazyString(name);
        this.__variables = new Variables();
        this.__base_url = new LazyString("");
        this.__verify = false;
        this.__export = new Export();
        this.__weight = 1;
        this.timeOut = 20;
    }

    public Config variables(String variablesStr){
        Variables tmpVar = new Variables(variablesStr);
        this.__variables.update(tmpVar);
        return this;
    }

    public Config base_url(String base_url){
        this.__base_url = new LazyString(base_url);
        return this;
    }

    public Config verify(Boolean verify){
        this.__verify = verify;
        return this;
    }

    public Config export(String export_var_name_str){
        this.__export.update(new Export(export_var_name_str));
        return this;
    }

    public Config withLocalDebug(Boolean isDebug){
        if(isDebug)
            logger.warn("已开启代理模式，所有请求将请求到 127.0.0.1:8888，请确认代理服务器状态。");

        this.isDebug = isDebug;
        return this;
    }

    public Config withCatchAllExpection(Boolean catchAllExpection){
        this.catchAllExpection = catchAllExpection;
        return this;
    }

    public Config withTimeOut(Integer timeOut){

        this.timeOut = timeOut;
        return this;
    }

    public TConfig perform(){
        return new TConfig(
                this.__name,
                this.__base_url,
                this.__verify,
                this.__variables,
                this.__export,
                this.__path,
                this.__weight
        );
    }
}
