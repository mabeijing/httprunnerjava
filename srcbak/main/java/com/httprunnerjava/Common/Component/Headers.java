package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.Intf.ParseableIntf;
import com.httprunnerjava.Common.Component.LazyContent.LazyContent;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Utils.CommonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Headers implements Serializable, ParseableIntf {
    private HashMap<String, LazyContent> content = new HashMap<>();

    public Headers(){}

    public Headers(String str){
        this(CommonUtils.parseJsonStrToMap(str));
    }

    public Headers(Map<Object,Object> raw_headers) {
        for (Map.Entry entry : raw_headers.entrySet()) {
            if (entry.getValue() instanceof String) {
                if (String.valueOf(entry.getKey()).toLowerCase().contains("content-type") || String.valueOf(entry.getKey()).toLowerCase().contains("contenttype"))
                    content.put("Content-Type", new LazyString(String.valueOf(entry.getValue())));
                else
                    content.put(String.valueOf(entry.getKey()), new LazyString(String.valueOf(entry.getValue())));
            }
            else
                content.put(String.valueOf(entry.getKey()), new LazyContent(entry.getValue()));
        }
    }

    public Headers(okhttp3.Headers raw_headers){
        raw_headers.forEach( each -> {
            if(each.getSecond() instanceof String)
                content.put(String.valueOf(each.getFirst()), new LazyString(String.valueOf(each.getSecond())));
            else
                content.put(String.valueOf(each.getFirst()), new LazyContent(each.getSecond()));
        });
    }


//    public void parse(Set check_variables_set) {
//        if(this.content == null || this.content.size() == 0)
//            return;
//
//        for(LazyContent value : content.values()){
//            if(value instanceof LazyString)
//                ((LazyString)value).parse(check_variables_set);
//        }
//    }

//    @Override
//    public ParseableIntf parse(Variables variables_mapping) {
//        if(this.content == null || this.content.size() == 0)
//            return new Headers();
//
//        for(LazyContent value : content.values()){
//            if(value instanceof LazyString)
//                ((LazyString)value).parse_string(variables_mapping, null);
//        }
//
//        return this;
//    }

    @Override
    public Headers to_value(Variables variables_mapping, Class function) {
        if(this.content == null || this.content.size() == 0)
            return this;

        for(LazyContent value : content.values()){
            if(value instanceof LazyString)
                ((LazyString)value).to_value(variables_mapping, function);
        }
        return this;
    }

    public Boolean isEmpty(){
        return (content == null || content.size() == 0);
    }

    public Map<String,String> toMap(){
        Map<String,String> headerMap = this.content.entrySet().stream().collect(
                Collectors.toMap(
                        entry -> entry.getKey(), entry -> String.valueOf(Optional.ofNullable(entry.getValue().getEvalValue()).orElse(""))
                )
        );

        return headerMap;
    }

    public void update(Headers headers){
        this.getContent().putAll(headers.getContent());
    }

    public void setdefault(String key,String value){
        this.content.put(key,new LazyString(value));
    }

    public String toString(){
        StringBuffer result = new StringBuffer("{\n");
        this.getContent().forEach( (k,v) -> result.append("    " + k + ":" + v.getEvalValue() + "\n"));
        result.append("\n}");
        return result.toString();
    }
}
