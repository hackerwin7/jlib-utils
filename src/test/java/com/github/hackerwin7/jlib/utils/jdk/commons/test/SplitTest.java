package com.github.hackerwin7.jlib.utils.jdk.commons.test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/13
 * Time: 3:12 PM
 * Desc:
 * Tips:
 */
public class SplitTest {

    public int a;
    public double b;
    private String c;

    public static void main(String[] args) {
        String ss = "1=2=3=4=5=6=dddd=ffff";
        System.out.println(Arrays.asList(ss.split("=", 2)));
        System.out.println(SplitTest.class.getFields().length);
        System.out.println(Arrays.asList(SplitTest.class.getFields()));
    }
}
