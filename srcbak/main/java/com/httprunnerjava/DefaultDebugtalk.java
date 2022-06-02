package com.httprunnerjava;

import java.util.Arrays;
import java.util.List;

public class DefaultDebugtalk {

    private static String __version__ = "1.0.0";

    public static String get_httprunner_version(){
        return __version__;
    }

    public static List<String> get_app_version(){
        return Arrays.asList("3.1","3.0");
    }

    public static Double sum_two(String m, String n) {
        return Double.valueOf(m) + Double.valueOf(n);
    }

    public static String funcWithoutParam(){
        return "android_chuizi";
    }

    public static String funcWithParam(String var1,String var2){
        return var1+var2;
    }

    public void setup_hooks(){
        System.out.println("setup_hooks execute");
    }

    public void setup_testsuite(){
        System.out.println("setup_testsuite execute");
    }

    public void setup_testcase(){
        System.out.println("setup_testcase execute");
    }

    public void setup_api(){
        System.out.println("setup_api execute");
    }

    public void teardown_hooks(){
        System.out.println("teardown_hooks execute");
    }

    public void teardown_testsuite(){
        System.out.println("teardown_testsuite execute");
    }

    public void func2(){
        System.out.println("func2 execute");
    }

    public void sleep(String n_secs){
        System.out.println("start sleep!");
        try{
            Thread.sleep(Long.valueOf(n_secs)*1000);
        }catch (Exception e){
        }
    }

    public Integer getSleepTime(String n_secs){
        return Integer.valueOf(n_secs);
    }
}
