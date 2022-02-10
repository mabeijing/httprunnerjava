package com.httprunnerjava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class runtest {
    private static Logger logger = LoggerFactory.getLogger(runtest.class);

    //TODO:
    // 可以实现的内容：
    // 1.指定目录执行
    // 2.指定文件执行
    // 3.不指定任何内容，默认执行testsuite目录下所有内容

    public static void main(String[] args){
        logger.info("start run testcase");
        Cli cli = new Cli();
        cli.mainEntrance();
    }
}
