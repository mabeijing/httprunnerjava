package com.httprunnerjava.Common.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.httprunnerjava.Common.Component.Intf.ParseableIntf;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Data
public class ReqJson implements ParseableIntf {

    private static Logger logger = LoggerFactory.getLogger(ReqJson.class);

    private static Integer MAP_OBJ_TYPE = 1;

    private static Integer LIST_OBJ_TYPE = 2;

    private static Integer STRING_OBJ_TYPE = 2;

    private JSONObject mapObj;

    private JSONArray listObj;

    private LazyString strObj;

    private Integer type;

    public ReqJson(String raw_req_json){
        try{
            mapObj = JSONObject.parseObject(raw_req_json);
            type = MAP_OBJ_TYPE;
        }catch (JSONException e1){
            try{
                listObj = JSONArray.parseArray(raw_req_json);
                type = LIST_OBJ_TYPE;
            }catch (Exception e2){
                strObj = new LazyString(raw_req_json);
                type = STRING_OBJ_TYPE;
            }
        }
    }

    @Override
    public ParseableIntf to_value(Variables variables_mapping, Class functions_mapping) {
        if(Objects.equals(type, STRING_OBJ_TYPE)) {
            String parsedStringObj = this.strObj.to_value(variables_mapping, functions_mapping).getEvalString();
            parseSrcString(parsedStringObj);
        }else if(Objects.equals(type, MAP_OBJ_TYPE)){
            this.mapObj = parseJson(this.mapObj,variables_mapping, functions_mapping);
        }else{
            this.listObj = parseJson(this.listObj,variables_mapping, functions_mapping);
        }
        return this;
    }

    public JSONObject parseJson(JSONObject jsonObject, Variables variables_mapping, Class functions_mapping){
        JSONObject result = new JSONObject();
        for(String s : jsonObject.keySet()){
            String newKey = new LazyString(s).to_value(variables_mapping, functions_mapping).getEvalString();
            if(jsonObject.get(s) instanceof JSONArray){
                result.put(newKey,parseJson((JSONArray)jsonObject.get(s), variables_mapping, functions_mapping));
            }else if(jsonObject.get(s) instanceof JSONObject){
                result.put(newKey,parseJson((JSONObject)jsonObject.get(s), variables_mapping, functions_mapping));
            }else if(jsonObject.get(s) instanceof String){
                String newValue = new LazyString((String)jsonObject.get(s)).to_value(variables_mapping, functions_mapping).getEvalString();
                result.put(newKey, newValue);
            }else {
                result.put(newKey,jsonObject.get(s));
            }
        }
        return result;
    }

    public JSONArray parseJson(JSONArray jsonArray, Variables variables_mapping, Class functions_mapping){
        JSONArray result = new JSONArray();
        for(Object s : jsonArray){
            if(s instanceof JSONArray){
                result.add(parseJson((JSONArray)s, variables_mapping, functions_mapping));
            }else if(s instanceof JSONObject){
                result.add(parseJson((JSONObject)s, variables_mapping, functions_mapping));
            }else if(s instanceof String){
                String newValue = new LazyString((String)s).to_value(variables_mapping, functions_mapping).getEvalString();
                result.add(newValue);
            }else {
                result.add(s);
            }
        }

        return result;
    }

    public void parseSrcString(String parsedStringObj){
        try{
            mapObj = JSONObject.parseObject(parsedStringObj);
            type = MAP_OBJ_TYPE;
        }catch (Exception e1){
            try{
                listObj = JSONArray.parseArray(parsedStringObj);
                type = LIST_OBJ_TYPE;
            }catch (Exception e2){
                logger.error("解析json出现问题，该字符串无法解析成JSONObject或者JSONArray，" +
                        "该报错可能是因为json串中引号使用不当造成，请参考github示例类request_with_variables_test");
                logger.error("待解析的字符串是：" + parsedStringObj);
                HrunExceptionFactory.create("E0070");
            }
        }
    }

    public String getEvalString(){
        if(Objects.equals(type, STRING_OBJ_TYPE))
            return this.getStrObj().getEvalString();
        else if(Objects.equals(type, LIST_OBJ_TYPE))
            return JSON.toJSONString(this.listObj);
        else
            return JSON.toJSONString(this.mapObj);
    }

}
