package com.httprunnerjava;

import com.google.common.base.Strings;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Model.ProjectMeta;
import com.httprunnerjava.Utils.ClassUtil;
import com.httprunnerjava.builtin.CSVFileUtil;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.httprunnerjava.Debugtalk;

public class Loader {

    private static final Logger logger = LoggerFactory.getLogger(Loader.class);

    //TODO：如果以后想做web化，这里的project_mata应该改成线程变量（threadlocal？）
    public static ProjectMeta project_meta;

    /*
    load_project_meta主要内容是初始化一个projectmeta对象，根目录，以及加载debugtalk文件
    根目录用来作什么？比如输出，日志，upload文件的加载等，都需要依赖根目录进行

    加载testcases路径，.env，debugtalk文件等
    testcases文件夹是一个相对路径变量
    默认情况下，project_meta只会调用加载一次

        load testcases, .env, debugtalk.py functions.
        testcases folder is relative to project_root_directory
        by default, project_meta will be loaded only once, unless set reload to true.

    Args:
        test_path (str): test file/folder path, locate project RootDir from this path.
        reload: reload project meta if set true, default to false

    Returns:
        project loaded api/testcases definitions,
            environments and debugtalk.py functions.

    */
    public static ProjectMeta load_project_meta (LazyString test_path){
        /*test_path不指定的时候，有两种加载方式：
            1.加载当前执行的命令行的路径
            2.指定log路径，debugtalk文件的路径进行加
        test_path指定的时候，加载指定路径下的内容就可以了
        TODO: 高优先级 指定文件路径的方法需要实现，当前版本只实现了加载默认路径或者指定路径下的内容
        在v1版本中，只实现代码调用形式的用例，所以对于path的加载和debugtalk.java文件的自动编译，都先不做了，等到v2版本在考虑
         */
        if(test_path == null) {
            return load_project_meta(null, false);
        }
        return load_project_meta(test_path.getRaw_value(),false);
    }

    public static ProjectMeta load_project_meta (String testPackagePath, boolean reload){
        if(project_meta != null && !reload)
            return project_meta;

        project_meta = new ProjectMeta();
        if(!Strings.isNullOrEmpty(testPackagePath))
            project_meta.setFunctions(ClassUtil.getDefaultDebugtalkClass(testPackagePath));

        return project_meta;
    }

    /*
    public static Pair<String, String> locate_project_root_directory(String test_path){
        test_path = prepare_path(test_path);

        String debugtalk_path = locate_debugtalk_py(test_path);
        String project_root_directory;

        if(!Strings.isNullOrEmpty(debugtalk_path)){
            project_root_directory = new File(debugtalk_path).getParentFile().getAbsolutePath();
        }else{
            project_root_directory = new File("").getAbsolutePath();
        }

        Pair<String, String> result = new Pair<>(debugtalk_path, project_root_directory);

        return result;
    }

    public static String prepare_path(String path){
        File file = new File(path);
        if(!file.exists()){
            logger.error("path not exist:" + path);
            HrunExceptionFactory.create("E0004");
        }

        return file.getAbsolutePath();
    }

    public static String locate_debugtalk_py(String start_path){
        String debugtalk_path = "";
        try{
            debugtalk_path = locate_file(start_path, "debugtalk.py");
        }catch(Exception e){
        }
        return debugtalk_path;
    }

    public static String locate_file(String start_path, String file_name) {
        File file = new File(start_path);
        String start_dir_path = "";
        if (file.isFile())
            start_dir_path = file.getParentFile().getAbsolutePath();
        else if (file.isDirectory())
            start_dir_path = file.getAbsolutePath();
        else {
            logger.error("invalid path: " + start_path);
            HrunExceptionFactory.create("E0006");
        }

        Path path = Paths.get(start_dir_path).resolve(file_name);
        File resultFile = new File(path.toString());
        if (resultFile.exists() && resultFile.isFile())
            return resultFile.getAbsolutePath();

        // locate recursive upward
        return locate_file(new File(start_dir_path).getParentFile().getAbsolutePath(), file_name);
    }*/

    public static Class<?> load_builtin_functions() {
        return load_module_functions("builtin");
    }

    public static Class<?> load_module_functions(String module) {
        return CompilerFile.loadClass(module);
    }

    public static List<Map<String,String>> load_csv_file(String csv_file){
        Path path = Paths.get(csv_file);
        //TODO:暂时只支持加载resource目录下的文件
        Loader loader = new Loader();
        InputStream inputStream = loader.getClass().getClassLoader().getResourceAsStream(csv_file);

        List<String> lines = CSVFileUtil.getLines(inputStream, "UTF-8");
        List<Map<String, String>> mapList = CSVFileUtil.parseList(lines);
        System.out.println(Arrays.toString(mapList.toArray()));
        return mapList;
    }

}
