package com.httprunnerjava;

import com.alibaba.fastjson.JSON;
import com.httprunnerjava.Common.Component.Intf.ParseableIntf;
import com.httprunnerjava.Common.Component.LazyContent.LazyContent;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Component.Variables;
import com.httprunnerjava.Common.Model.ProjectMeta;
import com.httprunnerjava.Utils.CommonUtils;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import com.httprunnerjava.exceptions.VariableNotFound;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Parse {

    static Logger logger = LoggerFactory.getLogger(Parse.class);

    //use $$ to escape $ notation
    static public Pattern dolloar_regex_compile = Pattern.compile("\\$\\$.*");

    //variable notation, e.g. ${var} or $var
    static public Pattern variable_regex_compile = Pattern.compile("\\$\\{(\\w+)}|\\$(\\w+).*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    //function notation, e.g. ${func1($var_1, $var_3)}
    static public Pattern function_regex_compile = Pattern.compile("(\\$\\{(\\w+)\\(([$\\w.\\-/\\s=,]*)\\)}).*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    static public Pattern full_function_regex_compile = Pattern.compile("\\$\\{(\\w+)\\(([$\\w.\\-/\\s=,]*)\\)}");

    public static Variables parse_variables_mapping(Variables variables_mapping){
        return parse_variables_mapping(variables_mapping,null);
    }

    //TODO:FunctionsMapping未实现
    /*parse_variables_mapping方法的作用是什么？
        比如传入的是  {"foo1": "testcase_config_bar1", "foo2": "testcase_config_bar2", "foo3": "this is $foo2"}
        foo3引用了foo2变量，最终会解析，返回这样的数据
        {"foo1": "testcase_config_bar1", "foo2": "testcase_config_bar2", "foo3": "this is testcase_config_bar2"}
     */
    public static Variables parse_variables_mapping(Variables variables_mapping, Class functions_mapping){
        Variables parsed_variables = new Variables();

        while(!Objects.equals(parsed_variables.getSize(), variables_mapping.getSize())){
            for(String var_name : variables_mapping.getKeys()){
                if(parsed_variables.getKeys().contains(var_name))
                    continue;

                LazyContent var_value = variables_mapping.get(var_name);
                Set<String> variables = extract_variables(var_value);

                if (variables.contains(var_name)) {
                    logger.error("参数" + var_name + "存在重复包含");
                    HrunExceptionFactory.create("E0047");
                }

                List<String> not_defined_variables =
                        variables.stream().filter( e->
                                !variables_mapping.getContent().containsKey(e)
                        ).collect(Collectors.toList());

                if(!not_defined_variables.isEmpty()){
                    logger.error("参数" + var_name + "不存在，请确认！！！");
                    HrunExceptionFactory.create("E0047");
                }

                Object parsed_value = null;
                try{
                    if (var_value instanceof LazyString) {
                        parsed_value = parse_data((LazyString) var_value, parsed_variables,functions_mapping).getEvalValue();
                    } else{
                        parsed_value = parse_data(var_value, parsed_variables,functions_mapping).getEvalValue();
                    }
                }
                catch(VariableNotFound e){
                    continue;
                }

                //TODO:其实这里的逻辑有点重复，以后可以进行调整，在前面的逻辑中，一个LazyString实际上已经被解析完成了，
                // 但是这里又重新定义了一次，下次如果需要用到这个变量，又要重新解析一次了
                if(parsed_value instanceof Map || parsed_value instanceof List)
                    parsed_variables.setVariables(var_name, JSON.toJSONString(parsed_value));
                else
                    parsed_variables.setVariables(var_name, parsed_value);
            }
        }

        return parsed_variables;
    }

    public static Set<String> extract_variables(LazyContent content){
        // extract all variables in content recursively.
        // TODO：这里应该支持加载list set map 等类型的数据的，，但是现在不支持需要后续支持
        if(content instanceof LazyString){
            return regex_findall_variables(((LazyString) content).getRaw_value());
        }

        return new HashSet<>();
    }

    public static Set<String> regex_findall_variables(String content){
        Set<String> result = new HashSet<>();
        int match_start_position = 0;

        while(match_start_position < content.length()){
            Matcher var_match = variable_regex_compile.matcher(content);
            if(var_match.find(match_start_position)) {
                String var_name;
                if(var_match.group(1) == null || var_match.group(1).equals(""))
                    var_name = var_match.group(2);
                else
                    var_name = var_match.group(1);

                result.add(var_name);
            }

            int curr_position = match_start_position;
            match_start_position = content.indexOf("$", curr_position+1);
            if(match_start_position == -1){
                content = content.substring(curr_position);
                match_start_position = content.length();
            }else{
                content = content.substring(curr_position,match_start_position);
            }
        }

        return result;
    }


    public static LazyString parse_data(LazyString raw_data, Variables variables_mapping,Class functions_mapping) {
        // parse raw data with evaluated variables mapping.
        // Notice: variables_mapping should not contain any variable or function.
        return raw_data.to_value(variables_mapping, functions_mapping);
    }

    public static LazyContent parse_data(LazyContent raw_data, Variables variables_mapping,Class functions_mapping) {
        // parse raw data with evaluated variables mapping.
        // Notice: variables_mapping should not contain any variable or function.
        if(raw_data instanceof LazyString){
            return parse_data((LazyString)raw_data, variables_mapping, functions_mapping);
        }
        return raw_data.to_value(variables_mapping,functions_mapping);
    }

    public static ParseableIntf parse_data(ParseableIntf raw_data, Variables variables_mapping, Class functions_mapping){
        // parse raw data with evaluated variables mapping.
        // Notice: variables_mapping should not contain any variable or function.
        return raw_data.to_value(variables_mapping, functions_mapping);
    }

    public static List<LazyContent> parse_data(List<LazyContent> raw_data, Variables variables_mapping, Class functions_mapping){
        // parse raw data with evaluated variables mapping.
        // Notice: variables_mapping should not contain any variable or function.
        List<LazyContent> result = new ArrayList<LazyContent>();
        for(LazyContent each : raw_data){
            result.add(parse_data(each,variables_mapping,functions_mapping));
        }
        return result;
    }

    public static Method get_mapping_function(String function_name,Class functions_mapping) {
        //如果这里仅仅是根据方法名去查询，场景有三：
        // 1.查询的方法是buildin方法，比如equals less_than等
        // 2.查询的方法是debugtalk.java内部定义的方法，但是该方法没有参数
        // 3.查询的方法是debugtalk.java内部定义的方法，但是该方法是有参数的，但是这里仅仅是根据方法名去查询了
        // 并没有做参数的匹配，也就是说，debugtalk文件不支持方法的重载
        Method method = null;
        //TODO:看看能否优化这里的流程

        //TODO:方法名如果是parameterize 或者environ或者multipart_encoder等等，需要解析
        if(Stream.of("parameterize", "P").collect(Collectors.toList()).contains(function_name)){
            try{
                return Loader.class.getMethod("load_csv_file",String.class);
            }catch (Exception e){
                //TODO:
                return null;
            }
        }

        //TODO:hrun这里采用的是多层调用，获取builtin模块的各个方法，但是java与此不同的是，builtin中的
        // comparator类是一个泛型类，经过测试，虽然能够获取到对应名字的方法，但是执行的时候，需要先实例化一个对象才可以
        try{
            Class<?> built_in_functions = Loader.load_builtin_functions();
            Class[] classed = new Class[2];
            classed[0] = Object.class;
            classed[1] = Object.class;
            method = built_in_functions.getMethod(function_name,classed);
        }catch(Exception e){
            log.debug(String.format("方法 %s 在builtin中没有找到",function_name));
        }

        //TODO：debugtalk中的函数不支持函数重载
        if(method == null) {
            for (Method each : functions_mapping.getMethods()) {
                if (each.getName().equals(function_name)) {
                    method = each;
                    return method;
                }
            }
        }

        if(method == null) {
            logger.error("方法 " + function_name + " 不存在");
            HrunExceptionFactory.create("E0024");
        }
        return method;
    }


    public static List<Map<String, Object>> parse_parameters(Map<String,Object> parameters){
        List<List<Map<String,Object>>> parsed_parameters_list = new ArrayList<>();

        // load project_meta functions
        ProjectMeta project_meta = Loader.load_project_meta(null);
        Class functions_mapping = project_meta.getFunctions();


        for(Map.Entry<String,Object> each : parameters.entrySet()) {
            List<Map<String,Object>> parameter_content_list = new ArrayList<>();
            String[] parameter_name_list = each.getKey().split("-");

            if (each.getValue() instanceof List) {
                //# (1) data list
                //# e.g. {"app_version": ["2.8.5", "2.8.6"]}
                //#       => [{"app_version": "2.8.5", "app_version": "2.8.6"}]
                //# e.g. {"username-password": [["user1", "111111"], ["test2", "222222"]}
                //#       => [{"username": "user1", "password": "111111"}, {"username": "user2", "password": "222222"}]

                for (Object parameter_item : (List) each.getValue()) {
                    List<Object> parameter_item_list = null;
                    if (!(parameter_item instanceof List))
                        parameter_item_list = Collections.singletonList(parameter_item);

                    Map<String, Object> parameter_content_dict = new HashMap<>();
                    for (int index = 0; index < parameter_name_list.length; index++) {
                        parameter_content_dict.put(parameter_name_list[index], parameter_item_list.get(index));
                    }
                    parameter_content_list.add(parameter_content_dict);
                }
            } else if (each.getValue() instanceof String) {
                Object parsed_parameter_content_temp =
                        parse_data(new LazyString(String.valueOf(each.getValue())), new Variables(), functions_mapping)
                                .getSpec_eval_value();
                if (!(parsed_parameter_content_temp instanceof List)) {
                    HrunExceptionFactory.create(("E0066"));
                }
                List parsed_parameter_content = (List) parsed_parameter_content_temp;
                for (Object parameter_item : parsed_parameter_content) {
                    Map<String, Object> parameter_dict = new HashMap<>();
                    if (parameter_item instanceof Map) {
                        for (String each2 : parameter_name_list) {
                            parameter_dict.put(each2, ((Map) parameter_item).get(each2));
                        }
                    } else if (parameter_item instanceof List) {
                        if (parameter_name_list.length == ((List<?>) parameter_item).size()) {
                            for (int index = 0; index < parameter_name_list.length; index++) {
                                parameter_dict.put(parameter_name_list[index], ((List) parameter_item).get(index));
                            }
                        } else {
                            HrunExceptionFactory.create("E0066");
                        }
                    } else if (parameter_name_list.length == 1) {
                        parameter_dict.put(parameter_name_list[0], parameter_item);
                    } else {
                        HrunExceptionFactory.create("E0066");
                    }

                    parameter_content_list.add(parameter_dict);
                }

            } else {
                HrunExceptionFactory.create("E0066");
            }

            parsed_parameters_list.add(parameter_content_list);
        }

        return CommonUtils.gen_cartesian_product(parsed_parameters_list);
    }

}
