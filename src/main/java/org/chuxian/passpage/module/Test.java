package org.chuxian.passpage.module;

import cn.hutool.core.util.ReUtil;

public class Test {
    public static void main(String[] args) {
//        int a = 226;
//        int x = a * 6;
//        for (int i = 0; i < 1000; i++) {
//            x = (int)(0.5 * x + 3.5 * a);
//            System.out.println(x);
//        }
//        int a = 450;
//        for (int i = 1; i <= 20; i++) {
//            a = (int)(a * 0.75);
//            System.out.println(i + "---" + a);
//        }
        String[] correctFilePathNames = "[webpage/www.sohu.com/displayRaw/144db9c8b9e04bf587b61f0352ef2715.html, webpage/www.sohu.com/displayRaw/77d7fb5ced864e2dbafea8b41e22c75c.html]".split("[, \\[\\]]");
        for (String correctFilePathName : correctFilePathNames) {
            if (correctFilePathName.length() > 0) {
                String correctFileName = ReUtil.get("display.*?/(.*?.html)", correctFilePathName, 1);
                System.out.println(correctFileName);
            }
        }
    }
}
