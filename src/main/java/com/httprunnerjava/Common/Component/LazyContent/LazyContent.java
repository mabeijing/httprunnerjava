package com.httprunnerjava.Common.Component.LazyContent;

import com.httprunnerjava.Common.Component.Variables;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;

import static com.httprunnerjava.Parse.*;

@Data
public class LazyContent<T> implements Cloneable, Serializable {
    //用来记录原始值，类型不确定，所以使用泛型存储
    public T raw_value;

    //new一个新的懒加载对象，对于非String类型来说，LazyContent只需要存储原始值就可以了
    //但是这里需要注意只能保存基本数据类型，如果用来构造的原始值本身是个类对象，可能会有潜在的指针问题
    public LazyContent(T t){
        this.raw_value = t;
    }

    //用来计算实际懒加载对象所存储的值，但是实际上对于LazyContent对象来说没啥用，只针对于LazyString类型需要进行计算
    //这里写一个对象也只是为了兼容LazyContent调用的时候不会报错
    public LazyContent<T> to_value(Variables variables_mapping, Class<?> functions_mapping) {
        if(raw_value instanceof List){
            for(LazyContent each : (List<LazyContent>)raw_value){
                each.to_value(variables_mapping,functions_mapping);
            }
        }else if(raw_value instanceof Map){
            for(Map.Entry<LazyContent,LazyContent> each : ((Map<LazyContent,LazyContent>)raw_value).entrySet()){
                each.getKey().to_value(variables_mapping,functions_mapping);
                each.getValue().to_value(variables_mapping,functions_mapping);
            }
        }

        return this;
    }

    //用来判断传入的String是否包含$或者变量 方法名
    /*
    public static Boolean is_var_or_func_exist(String content){
        int match_start_position = 0;

        match_start_position = content.indexOf("$", 0);
        if(match_start_position == -1)
            return false;


        while(match_start_position < content.length()){
            Matcher dollar_match = dolloar_regex_compile.matcher(content);
            if(dollar_match.find(match_start_position)){
                match_start_position = dollar_match.end();
                continue;
            }

            Matcher func_match = function_regex_compile.matcher(content);
            if(func_match.find(match_start_position)){
                return true;
            }

            Matcher var_match = variable_regex_compile.matcher(content);
            if(var_match.find(match_start_position)){
                return true;
            }
            return false;
        }

        return false;
    }*/

    //用来判断是否包含方法
    /*
    public static Boolean is_func_exist(String content){
        Matcher var_match = function_regex_compile.matcher(content);
        if(var_match.find()){
            return true;
        }
        return false;
    }*/

    /**
     * 获得LazyContent对象的原始值 raw_value
     */
    public T getRaw_value(){
        return this.raw_value;
    }

    /**
     * 获得LazyContent对象的实际值，如果对象是LazyString类型，返回其实际值realvalue，否则返回raw_value
     */
    public Object getEvalValue(){
        if(this instanceof LazyString)
            return ((LazyString)this).getEvalString();
        else if(raw_value instanceof List){
            List<Object> result = new ArrayList<>();
            for(LazyContent each : (List<LazyContent>)raw_value){
                result.add(each.getEvalValue());
            }
            return result;
        }else if(raw_value instanceof Map){
            for(Map.Entry<Object,Object> one : ((Map<Object,Object>) raw_value).entrySet()){
                if(one.getKey() instanceof String && !(one.getValue() instanceof LazyContent))
                    return this.raw_value;
                else
                    break;
            }
            Map<Object,Object> result = new HashMap<>();
            for(Map.Entry<LazyContent,LazyContent> each : ((Map<LazyContent,LazyContent>)raw_value).entrySet()){
                result.put(each.getKey().getEvalValue(),each.getValue().getEvalValue());
            }
            return result;
        }else
            return getRaw_value();
    }

    public static LazyContent loadObject(Object obj){
        if (obj instanceof String)
            return new LazyString(String.valueOf(obj));
        else if(obj instanceof LazyContent)
            return (LazyContent)obj;
        else if(obj instanceof List){
            List<Object> varList =  new ArrayList<>();
            for (Object each : (List)obj)
                varList.add(loadObject(each));
            return new LazyContent(varList);
        }
        else if(obj instanceof Map){
            //TODO：低优先级 感觉在实际的请求中，这里Map的key都会是String，所以下面的map能不能改成String？
            Map<Object,Object> varMap= new HashMap<>();
            for(Map.Entry each :((Map<?, ?>) obj).entrySet()){
                varMap.put(loadObject(each.getKey()),loadObject(each.getValue()));
            }
            return new LazyContent(varMap);
        }
        else
            return new LazyContent(obj);
    }

    @Override
    public LazyContent<T> clone() {
        try {
            LazyContent clone = (LazyContent) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
