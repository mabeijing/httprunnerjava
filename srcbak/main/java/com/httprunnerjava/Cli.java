package com.httprunnerjava;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class Cli {

    public void mainEntrance(){
        //TODO： 原版有一些命令行的解析，暂时先不做
        // 所有的命令函参数，暂时通过类的成员变量处理
        // parser = argparse.ArgumentParser(description=__description__)
        // parser.add_argument(
        //        "-V", "--version", dest="version", action="store_true", help="show version"
        //    )
        //
        //    subparsers = parser.add_subparsers(help="sub-command help")
        //    sub_parser_run = init_parser_run(subparsers)
        //    sub_parser_scaffold = init_parser_scaffold(subparsers)
        //    sub_parser_har2case = init_har2case_parser(subparsers)
        //    sub_parser_make = init_make_parser(subparsers)
        //
        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { HttpRunner.class });
        testng.addListener(tla);
        testng.run();

    }
}
