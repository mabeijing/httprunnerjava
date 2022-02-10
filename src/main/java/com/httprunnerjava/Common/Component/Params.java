package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.Intf.ParseableIntf;
import com.httprunnerjava.Common.Component.LazyContent.*;
import com.httprunnerjava.Utils.CommonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Params implements Serializable, ParseableIntf {
    private HashMap<String, LazyContent<?>> content = new HashMap<>();

    public Params(){}

    public Params(String str){
        this(CommonUtils.parseJsonStrToMap(str));
    }

    public Params(Map<Object,Object> raw_params) {
        for (Map.Entry<Object,Object> entry : raw_params.entrySet()) {
            if (entry.getValue() instanceof String )
                content.put(String.valueOf(entry.getKey()), new LazyString(String.valueOf(entry.getValue())));
            else
                content.put(String.valueOf(entry.getKey()), new LazyContent<>(entry.getValue()));
        }
    }

//    public void parse(Set check_variables_set) {
//        if(this.content == null || this.content.size() == 0)
//            return;
//
//        for(LazyContent<?> value : content.values()){
//            if(value instanceof LazyString)
//                ((LazyString)value).parse(check_variables_set);
//        }
//    }

//    @Override
//    public ParseableIntf parse(Variables variables_mapping) {
//        if(this.content == null || this.content.size() == 0)
//            return new Params();
//
//        for(LazyContent<?> value : content.values()){
//            if(value instanceof LazyString)
//                ((LazyString)value).parse_string(variables_mapping,null);
//        }
//
//        return this;
//    }

    public ParseableIntf to_value(Variables variables_mapping,Class function) {
        if(this.content == null || this.content.size() == 0)
            return this;

        for(LazyContent<?> value : content.values()){
            if(value instanceof LazyString)
                ((LazyString)value).to_value(variables_mapping, function);
        }

        return this;
    }

    public Boolean isEmpty(){
        return (content == null || content.size() == 0);
    }

    public void update(Params params){
        this.getContent().putAll(params.getContent());
    }
}