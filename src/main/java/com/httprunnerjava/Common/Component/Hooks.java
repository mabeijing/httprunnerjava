package com.httprunnerjava.Common.Component;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Hooks {

    @Data
    public class HookString{
        private Integer type;
        private LazyString funcHook;
        private Map<LazyString, LazyString> mapHook;
        //钩子函数失败后，是否可以跳过继续执行该case的标记
        private Boolean noThrowException;

        public HookString(Integer type, String hookStr,Boolean noThrowException){
            if(type == 1) {
                this.type = 1;
                funcHook = new LazyString(hookStr);
                this.noThrowException = noThrowException;
            }
            else if(type == 2){
                mapHook = new HashMap<>();
                this.type = 2;
                JSONObject temp = JSONObject.parseObject(hookStr);
                for(Map.Entry<String,Object> each : temp.entrySet()){
                    LazyString key = new LazyString(each.getKey());
                    LazyString value = new LazyString(each.getValue().toString());
                    mapHook.put(key,value);
                }
                this.noThrowException = noThrowException;
            }
        }

        public String toString(){
            if(type == 1){
                return funcHook.raw_value;
            }else{
                return "{" + mapHook.keySet().iterator().next().toString() + ":" +
                        getMapHook().values().iterator().next().raw_value + "}";
            }
        }
    };

    private List<HookString> content = new ArrayList<>();

    public void add(String raw_hook){
        try {
            JSONObject parsedStr = JSONObject.parseObject(raw_hook);
            if(parsedStr instanceof Map && parsedStr.size() == 1) {
                HookString hookString = new HookString(2, parsedStr.toJSONString(),false);
                content.add(hookString);
            }else{
                HrunExceptionFactory.create("E0067");
            }
        } catch (JSONException e) {
            HookString hookString = new HookString(1, raw_hook, false);
            content.add(hookString);
        }
    }

    public void addNoThrowException(String raw_hook){
        try {
            JSONObject parsedStr = JSONObject.parseObject(raw_hook);
            if(parsedStr instanceof Map && parsedStr.size() == 1) {
                HookString hookString = new HookString(2, parsedStr.toJSONString(),true);
                content.add(hookString);
            }else{
                HrunExceptionFactory.create("E0067");
            }
        } catch (JSONException e) {
            HookString hookString = new HookString(1, raw_hook, true);
            content.add(hookString);
        }
    }



    public Integer size(){
        return this.content.size();
    }




}
