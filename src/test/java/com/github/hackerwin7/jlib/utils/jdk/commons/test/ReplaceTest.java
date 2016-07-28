package com.github.hackerwin7.jlib.utils.jdk.commons.test;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/15
 * Time: 3:17 PM
 * Desc:
 */
public class ReplaceTest {
    public static void main(String[] args) throws Exception {
        String tid = "200217222156";
        String pid = tid.replaceFirst("2002", "2102");
        System.out.println(pid);
    }
}
