package com.httprunnerjava.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.httprunnerjava.exception.CompareError;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class JsonUtils {

    public static Map parseJsonStrToMap(String str){
        try {
            Map json = JSONObject.parseObject(str, Map.class);
            return json;
        }catch (Exception e){
            log.error("解析JSON中出现错误，待解析的字符串是：" + str);
            throw e;
        }
    }

    public static void compareJsons(JSONObject json1, JSONObject json2, String key){
        commonCompare(json1,json2,key);
        for (String s : json1.keySet()) {
            key = s;
            compareJsons(json1.get(key), json2.get(key), key);
        }
    }

    public static void compareJsons(Object json1,Object json2, String key){
        commonCompare(json1,json2,key);
        if (json1 instanceof JSONObject){
            //如果是JSONObject则继续递归比较。
            compareJsons((JSONObject) json1,(JSONObject) json2, key);
        } else if (json1 instanceof JSONArray){
            //如果是JSONArray，则进行数组类比较。
            compareJsons((JSONArray) json1,(JSONArray) json2,key);
        }  else {
            //其余全部为字符串比较，非字符串的也转换为字符串比较。
            compareJsons(json1.toString(),json2.toString(),key);
        }
    }

    public static void compareJsons(JSONArray jsonArray1, JSONArray jsonArray2, String key){
        commonCompare(jsonArray1,jsonArray2,key);
        //数组存在无序的情况，所以需要将1中的每一个元素，跟2中的所有元素进行比较。
        //两种方案：1.先对两个jsonArray进行排序，然后再依次比较。
        // 2.对1中的每一个元素，判断是否在2中存在。(有重复元素的可能会有问题。)
        //方案2的实现：
        for (Object o1 : jsonArray1) {
            if (!jsonArray2.contains(o1)) {
                log.error("不一致：key  " + key + " json1中的 jsonArray其中的value ： " + JSONObject.toJSONString(o1) + "  仅在json1中存在，不在json2中存在");
                throw new CompareError("JSON比对结果不一致");
            }
        }

        for (Object o2 : jsonArray2) {
            if (!jsonArray1.contains(o2)) {
                log.error("不一致：key " + key + " json2中的 jsonArray其中的value ： " + JSONObject.toJSONString(o2) + "  仅在json2中存在，不在json1中存在");
                throw new CompareError("JSON比对结果不一致");
            }
        }
    }

    public static void containJsonArray(JSONArray check_value, JSONArray expect_value, String key){
        //数组存在无序的情况，所以需要将1中的每一个元素，跟2中的所有元素进行比较。
        //两种方案：1.先对两个jsonArray进行排序，然后再依次比较。
        // 2.对1中的每一个元素，判断是否在2中存在。(有重复元素的可能会有问题。)
        //方案2的实现：
        boolean isDifferent = false;
        for (Object o1 : expect_value) {
            if (!check_value.contains(o1)) {
                log.error("不一致：key  " + o1 + " 接口返回的check_value并不包含该key");
                isDifferent = true;
            }
        }
        if(isDifferent)
            throw new CompareError("JSON比对结果不一致");
    }

    public static void compareJsons(String json1,String json2,String key){
        commonCompare(json1,json2,key);
        if (json1.equals(json2)){
            System.out.println("一致：key " + key + " ， json1 value = " + json1 + " json2 value = " + json2);
        } else {
            System.err.println("不一致： key " + key + " ， json1 value = " + json1 + " json2 value = " + json2 );
            throw new CompareError("JSON比对结果不一致");
        }

    }

    public static void commonCompare(Object json1,Object json2,String key){
        if (json1 == null && json2 == null){
            System.err.println("不一致： key " + key + " 在两者中均不存在");
        }
        if (json1 == null){
            System.err.println("不一致： key " + key + " 在json1中不存在，在json2中为 " + JSONObject.toJSONString(json2) );
            throw new CompareError("JSON比对结果不一致");
        }
        if (json2 == null){
            System.err.println("不一致： key " + key + " 在json1中为 " + JSONObject.toJSONString(json2) + " 在json2中不存在" );
            throw new CompareError("JSON比对结果不一致");
        }
    }


    public static Object getByNumKey(JSON data, String key){
        //两种形式，一种是传入jsonobject，key是数字型的字符串
        // 另一种是传入jsonarray，key是数字
        if(data instanceof JSONArray){
            return ((JSONArray)data).get(Integer.parseInt(key));
        }else {
            return ((JSONObject)data).get(key);
        }
    }

    public static JSON getSubJson(JSON data, String key){
        try{
            data = ((JSONObject)data).getJSONObject(key);
        }catch (Exception e){
            data = ((JSONObject)data).getJSONArray(key);
        }
        return data;
    }
}
