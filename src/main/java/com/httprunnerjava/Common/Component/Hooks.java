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

        public HookString(Integer type, String hookStr){
            if(type == 1) {
                this.type = 1;
                funcHook = new LazyString(hookStr);
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
            }
        }
    };

    private List<HookString> content = new ArrayList<>();

    public void add(String raw_hook){
        try {
            JSONObject parsedStr = JSONObject.parseObject(raw_hook);
            if(parsedStr instanceof Map && parsedStr.size() == 1) {
                HookString hookString = new HookString(2, parsedStr.toJSONString());
                content.add(hookString);
            }else{
                HrunExceptionFactory.create("E0067");
            }
        } catch (JSONException e) {
            HookString hookString = new HookString(1, raw_hook);
            content.add(hookString);
        }
    }

    public Integer size(){
        return this.content.size();
    }




}
