package com.bjpowernode.p2p.test;

import java.util.regex.Pattern;

/**
 * ClassName:MyMain
 * package:com.bjpowernode.p2p.test
 * Descrption:
 *
 * @Date:2018/7/14 10:02
 * @Author:guoxin
 */
public class MyMain {

    public static void main(String[] args) {
        System.out.println(Pattern.matches("(^[一-龥]{2,8}$)","张6"));
    }
}
