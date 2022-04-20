package com.httprunnerjava.model;

import com.httprunnerjava.DefaultDebugtalk;
import lombok.Data;

/**
 * @Author: ChuCan
 * @CreatedDate: 2022-04-07-1:43
 * @Description:
 */
@Data
public class ProjectMeta {

    private String RootDir;

    public static Class<?> functions;

    public void setFunctions(Class<?> cls) {
        functions = cls;
    }

    public Class<?> getFunctions() {
        return functions;
    }

    public ProjectMeta() {
        //TODO:默认加载debugtalk作为内部function对象
        functions = DefaultDebugtalk.class;
    }

}
