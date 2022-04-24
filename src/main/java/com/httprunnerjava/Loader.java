package com.httprunnerjava;

import com.google.common.base.Strings;
import com.httprunnerjava.model.ProjectMeta;
import com.httprunnerjava.model.lazyLoading.LazyString;
import com.httprunnerjava.utils.CSVFileUtil;
import com.httprunnerjava.utils.ClassUtils;
import com.httprunnerjava.utils.CompilerFile;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class Loader {

    // 这里和hrun原版稍有差别，如果以后想支持多线程执行，那么线程变量必不可少，其实这里也是一种推测，目前也用不到
    public static final ThreadLocal<ProjectMeta> projectMetaContext = new ThreadLocal<>();

    public static void setProjectMeta(final ProjectMeta projectMeta) {
        projectMetaContext.set(projectMeta);
    }

    public static ProjectMeta getProjectMeta() {
        return projectMetaContext.get();
    }

    public static void clear() {
        projectMetaContext.remove();
    }

    /**
     * TODO：目前在仅支持class执行用例的情况下，loadProjectMeta并无任何实际意义
    */
    public static void loadProjectMeta (LazyString test_path){

        if(test_path == null) {
            loadProjectMeta(null, false);
        }
        loadProjectMeta(test_path.getRawValue(),false);
    }

    public static void loadProjectMeta (String testPackagePath, boolean reload){
        if(getProjectMeta() != null && !reload)
            return;

        ProjectMeta projectMeta = new ProjectMeta();
        if(!Strings.isNullOrEmpty(testPackagePath))
            projectMeta.setFunctions(ClassUtils.getDefaultDebugtalkClass(testPackagePath));

        projectMeta.setEnvVar(loadEnvFile());
        projectMetaContext.set(projectMeta);
    }

    public static Class<?> loadBuiltinFunctions() {
        return load_module_functions("builtin");
    }

    public static Class<?> load_module_functions(String module) {
        return CompilerFile.loadClass(module);
    }

    public static Class<?> loadLoadFileClass(){
        return CompilerFile.loadLoadFileClass();
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

    public static Map<String,Object> loadEnvFile(){
        HashMap<String,Object> result; ;
        Yaml yaml = new Yaml();
        Map<String, Object> load = yaml.loadAs(new InputStreamReader(
                Objects.requireNonNull(HttpRunner.class.getResourceAsStream("/env.yml")), StandardCharsets.UTF_8),
                Map.class
        );
        return load;
    }


}
