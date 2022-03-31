package com.httprunnerjava.Common.Component.LazyContent;

import com.httprunnerjava.Common.Component.Intf.ParseableIntf;
import com.httprunnerjava.Common.Component.Variables;
import com.httprunnerjava.HttpSession;
import com.httprunnerjava.builtin.Comparator;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import com.httprunnerjava.exceptions.LazyStringParseError;
import com.httprunnerjava.exceptions.VariableNotFound;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.httprunnerjava.Parse.*;

public class LazyString extends LazyContent<String> implements Serializable, ParseableIntf {

    static Logger logger = LoggerFactory.getLogger(LazyString.class);

    public static Pattern integer_regex_compile = Pattern.compile("(\\d+)");

    public static Pattern double_regex_compile = Pattern.compile("(\\d+)\\.(\\d+)");

    private Class functions_mapping_ptr; //是一个指针地址，指向的是全局的functions_mapping

    private Set check_variables_set; // 用来保存上下文可用的所有变量，在加载lazyString的真正值时用得到

    private Boolean cached; //是否需要保存在缓存当中，这个暂时不实现TODO

    private String _string; //解析后的字符串

    private Object spec_eval_value;

    public LazyString(String s) {
        super(s);
    }


//    @Override
//    public void parse(Set check_variables_set) {
//    }
//
//    @Override
//    public ParseableIntf parse(Variables variables_mapping) {
//        return null;
//    }

    //TODO: 高优先级 这里暂时只做全字匹配，暂时保留根据传入的string进行不断拆解和计算的框架，但是实际上匹配一次就会continue了
    // 也就是说只能解析 $param  ${function()} 对于函数的传参暂时也不做,比如 $function($param1,#param2)}
    @Override
    public LazyString to_value(Variables variables_mapping, Class functions_mapping) {
        if(variables_mapping == null){
            variables_mapping = variables_mapping;
        }

        parse_string(variables_mapping,functions_mapping);
        return this;
    }

    public Object parse_string(Variables variables_mapping, Class functions_mapping){
    /*Examples:
        >>> raw_string = "abc${add_one($num)}def"
        >>> variables_mapping = {"num": 3}
        >>> functions_mapping = {"add_one": lambda x: x + 1}
        >>> parse_string(raw_string, variables_mapping, functions_mapping)
            "abc4def"
     */

        Integer match_start_position = 0;
        String remain_string = "";
        match_start_position = raw_value.indexOf("$");
        if(match_start_position != -1){
            this._string = raw_value.substring(0,match_start_position);
            remain_string = raw_value.substring(match_start_position);
        }else{
            this._string = raw_value;
            return this._string;
        }


        while(remain_string.length() != 0){
            Matcher dollar_match = dolloar_regex_compile.matcher(remain_string);
            if(dollar_match.matches()){
                remain_string = remain_string.substring(dollar_match.end());
                _string += "$";
                continue;
            }

            Matcher func_match = function_regex_compile.matcher(remain_string);
            if(func_match.matches()){
                String func_name = func_match.group(2);
                //TODO:string整体为一个方法名时，会出现死循环
                Method func = get_mapping_function(func_name, functions_mapping);

                String func_params_str = func_match.group(3);
                Map function_meta = parse_function_params(func_params_str);
                List<LazyContent> args = (ArrayList<LazyContent>)function_meta.get("argsList");
                Map kwargs = (Map)function_meta.get("kwargsMap");
                List<Object> parsed_args = parse_data(args, variables_mapping, functions_mapping).stream()
                        .map(e -> e.getEvalValue())
                        .collect(Collectors.toList());

                Object func_eval_value = null;
                try{
                    if(IsBuiltInFunc(func_name)){
                        Comparator comparator = new Comparator(parsed_args.get(0));
                        func_eval_value = func.invoke(comparator,parsed_args.get(0),parsed_args.get(1));
                    }else{
                        List<Object> funcParams = new ArrayList<Object>();
                        for(Object each : parsed_args){
                            if(each instanceof LazyString)
                                funcParams.add(((LazyString)((LazyString) each).parse_string(variables_mapping,functions_mapping)).getEvalValue());
                            else
                                funcParams.add(each);
                        }
                        Object obj = functions_mapping.newInstance();
                        if(funcParams.size() == 0){
                            func_eval_value =  func.invoke(obj);
                        }else {
                            func_eval_value = func.invoke(obj, funcParams.toArray());
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    throw new LazyStringParseError(e.getMessage());
                }

                String func_raw_str = "${" + func_name + "(" + func_params_str + ")}";
                if(func_raw_str.equals(raw_value.toString())){
                    this.spec_eval_value = func_eval_value;
                    this._string += String.valueOf(func_eval_value);
                    this.spec_eval_value = func_eval_value;
                    return this;
                }

                this._string += String.valueOf(func_eval_value);
                remain_string = remain_string.substring(func_match.group(1).length());
            }

             Matcher var_match = variable_regex_compile.matcher(remain_string);
            if(var_match.matches()){
                String var_name;
                if(var_match.group(1) == null || var_match.group(1).equals(""))
                    var_name = var_match.group(2);
                else
                    var_name = var_match.group(1);

                Object var_value = get_mapping_variable(var_name, variables_mapping);

                if(full_function_regex_compile.matcher(raw_value).matches())
                    return var_value;
                this._string += String.valueOf(var_value);
                remain_string = remain_string.substring(var_name.length()+1);
            }

            match_start_position = remain_string.indexOf("$");
            if(match_start_position == 0){
                logger.error("无法解析的字符串" + raw_value);
                logger.error("字符串中出现$但却无法解析，不属于$$ 变量或者方法的任何一种类型，请检查。");
                HrunExceptionFactory.create("E0065");
            }
            if(match_start_position != -1){
                this._string += remain_string.substring(0,match_start_position);;
                remain_string = remain_string.substring(match_start_position);
            }else{
                this._string += remain_string;
//                this._string += raw_value.substring(0,match_start_position);
                return this._string;
            }
        }
        return this._string;
    }

    public Object get_mapping_variable(String variable_name,Variables variables_mapping){
        try {
            return variables_mapping.get(variable_name).getEvalValue();
        }catch (Exception e){
            throw new VariableNotFound(String.format("%s not found in %s",variable_name,variables_mapping));
        }
    }

    public String getEvalString(){
        return this._string == null ? this.getRaw_value():this._string;
    }

    public static Map parse_function_params(String params) {
        Map<String, Object> function_meta = new HashMap<String, Object>() {{
            put("argsList", new ArrayList<LazyContent>());
            put("kwargsMap", new HashMap<String, Object>());
        }};

        String params_str = params.trim();
        if (params.equals(""))
            return function_meta;

        String[] args_list = params_str.split(",");
        for (String arg : args_list) {
            arg = arg.trim();
            if (arg.contains("=")) {
                String[] keyvalue = arg.split("=");
                if (keyvalue.length > 2)
                    HrunExceptionFactory.create("E0021");
//TODO:                ((Map) function_meta.get("kwargsMap")).put(keyvalue[0].trim(), parse_string_value(keyvalue[1].trim()));
            } else {
                if(arg instanceof String)
                    ((List) function_meta.get("argsList")).add(new LazyString(arg));
                else
                    ((List) function_meta.get("argsList")).add(new LazyContent(arg));
            }
        }
        return function_meta;
    }

    public static Boolean IsBuiltInFunc(String func_name){
        if(Arrays.asList(",less_than".split(",")).contains(func_name)){
            return true;
        }
        return false;
    }

    public Object getSpec_eval_value(){
        return this.spec_eval_value;
    }

    public String get_real_eval_string(){
//        if(){
//
//        }
        return this._string == null ? this.getRaw_value():this._string;
    }
}
