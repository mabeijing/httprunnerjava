package com.httprunnerjava.testdir;

import lombok.AllArgsConstructor;

public class noaddgit {

    public static class test2{
        public StringBuffer test2_str;
        public test2(Integer num,StringBuffer str){
            test2_str = new StringBuffer("");
            for(int i=0;i<num;i++){
                test2_str.append(str);
            }
        }
    }

    public test2 a;

    public static void main(String[] args){
        test2 test = new test2(1,new StringBuffer("1"));
        System.out.println(test.hashCode());
        test = new test2(3,new StringBuffer("123456"));
        System.out.println(test.hashCode());
        test = new test2(40000,new StringBuffer("2345678"));
        System.out.println(test.hashCode());
    }
}
