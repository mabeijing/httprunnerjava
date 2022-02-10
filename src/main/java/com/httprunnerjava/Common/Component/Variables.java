package com.httprunnerjava.Common.Component;

import com.httprunnerjava.Common.Component.Intf.ParseableIntf;
import com.httprunnerjava.Common.Component.LazyContent.LazyContent;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Model.ResponseObject;
import com.httprunnerjava.Utils.CommonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.*;


//TODO: variables 还有一种表现形式是直接使用${gen_variables()}这种方式获取
@Data
public class Variables implements Serializable, ParseableIntf {
    public HashMap<String, LazyContent> content = new HashMap<String,LazyContent>();

    //TODO：低优先级 这里单独存放了request 和 resp ，后面看看能否优化一下
//    public Request request;
//
//    public Response resp;

    //默认构造函数，用来构造一个空的variables对象
    public Variables(){
    }

    public Variables(String str){
        this(CommonUtils.parseJsonStrToMap(str));
    }

    //最常用的构造函数，在初始化一个新的变量对象时，需要用到此方法
    public Variables(Map<String,Object> raw_variables) {
        for (Map.Entry entry : raw_variables.entrySet()) {
            if (entry.getValue() instanceof String)
                content.put(String.valueOf(entry.getKey()), new LazyString(String.valueOf(entry.getValue())));
            else if(entry.getValue() instanceof LazyContent)
                content.put(String.valueOf(entry.getKey()),(LazyContent)entry.getValue());
            else if(entry.getValue() instanceof List)//可能list中的每个值都是laztString
                content.put(String.valueOf(entry.getKey()), LazyContent.loadObject(entry.getValue()));
            else if(entry.getValue() instanceof Map)//可能list中的每个值都是laztString
                content.put(String.valueOf(entry.getKey()), LazyContent.loadObject(entry.getValue()));
            else
                content.put(String.valueOf(entry.getKey()), new LazyContent(entry.getValue()));
        }
    }

    //支持不同的set方法，向content中put新值
    //比如直接传入一个新的map
    public void setVariables(Map<String,Object> raw_variables){
        for(Map.Entry<String,Object> entry : raw_variables.entrySet()){
            if (entry.getValue() instanceof String)
                content.put(entry.getKey(), new LazyString(String.valueOf(entry.getValue())));
            else
                content.put(entry.getKey(), new LazyContent(entry.getValue()));
        }
    }

    //或者传入一对key和value
    public void setVariables(String key, Object value){
        Map tmpMap = new HashMap<String,Object>();
        tmpMap.put(key,value);
        setVariables(tmpMap);
    }

    public ParseableIntf to_value(Variables variables_mapping, Class function) {
        //TODO:
        return null;
    }

    /**
     * 在原variables对象上进行扩展
     * @param another_var 另一个Var对象
     */
    public void extend(Variables another_var){
        Optional.ofNullable(another_var).ifPresent(
                a -> this.getContent().putAll(a.getContent())
        );
    }

    public void extend(Map param){
        Optional.ofNullable(param).ifPresent(
                a -> this.getContent().putAll(new Variables(param).getContent())
        );
    }

    //TODO：update 和extend实际是一样的，可以考虑缩减
    // 出现两者都有的原因是为了和hrun原版一致
    public Variables update(Variables variables){
        extend(variables);
        return this;
    }

    public Variables update(Extract extract){
        //TOOD：高优先级 待实现
        return this;
    }

    public Variables update(Map param){
        extend(param);
        return this;
    }

    //TODO： 低优先级下面两个方法能不能合并下
    public Variables update(String key,TRequest value){
        this.content.put(key, new LazyContent(value));
        return this;
    }

    public Variables update(String key, ResponseObject value){
        this.content.put(key,new LazyContent(value));
        return this;
    }

    /**
     * 合并两个Variables对象然后返回合并后的Variables对象
     * @param var2
     * var2 的优先级比 var1 高
     */
    public static Variables extend2Variables(Variables var1,Variables var2){
        Variables override_variables_mapping = CommonUtils.deepcopy_obj(Optional.ofNullable(var1).orElse(new Variables()));
        override_variables_mapping.extend(CommonUtils.deepcopy_obj(var2));

        return override_variables_mapping;
    }

    /**
     * 静态方法，判断Var对象中的variables变量是否为空，静态方法的目的是防止参数本身就是空的
     */
    public static Boolean isNullOrEmpty(Variables variables){
        return variables == null || variables.getContent().isEmpty();
    }

    /**
     * 非静态方法，判断Var对象中的variables变量是否为空
     */
    public Boolean isEmpty(){
        return this.content.isEmpty();
    }

    public LazyContent getVariable(String name){
        //TODO: 获得variables中的某个value，但是如果value是$var形式，需要再次转化或者什么
        if(this.content.get(name) != null){
            return this.content.get(name);
        }else{
            return null;
        }
    }

    public Integer getSize(){
        return this.content.size();
    }



    public Set<String> getKeys(){
        return this.content.keySet();
    }

    public LazyContent get(String key){
        return this.content.get(key);
    }

    public Map<String,Object> translateToMap(){
        Map<String,Object> result = new HashMap<>();
        for(Map.Entry<String,LazyContent> each : this.getContent().entrySet()){
            result.put(each.getKey(),each.getValue().getEvalValue());
        }
        return result;
    }

}
