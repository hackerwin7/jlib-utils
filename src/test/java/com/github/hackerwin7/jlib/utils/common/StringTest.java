package com.github.hackerwin7.jlib.utils.common;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2017/01/17
 * Time: 11:27 AM
 * Desc:
 * Tips:
 */
public class StringTest {
    public static void main(String[] args) throws Exception {
        StringTest st = new StringTest();
        st.test();
    }

    public void test() {
        String s = "-Xmx2048.89m";
        System.out.println(s.replaceAll("[\\s+a-zA-Z :^-]",""));
    }
}
