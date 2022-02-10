package com.httprunnerjava.Common.Model.TModel;

import com.httprunnerjava.Common.Component.Export;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Component.Parameters;
import com.httprunnerjava.Common.Component.Variables;
import lombok.Data;

@Data
public class TConfig {

    private LazyString name;
    private Boolean verify;
    private LazyString base_url;
    private Variables variables;
    private Parameters parameters;
    /*TODO:
    private Hooks setup_hooks;
    private Hooks teardown_hooks;
     */
    private Export export;
    private LazyString path;
    private int weight;

    public TConfig(LazyString name, LazyString base_url, Boolean verify, Variables variables, Export export, LazyString path,
                   int weight){
        this.name = name;
        this.base_url = base_url;
        this.verify = verify;
        this.variables = variables;
        this.export = export;
        this.path = path;
        this.weight = weight;

    }

}
