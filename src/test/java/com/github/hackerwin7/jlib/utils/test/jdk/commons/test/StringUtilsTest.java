package com.github.hackerwin7.jlib.utils.test.jdk.commons.test;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/20
 * Time: 11:05 AM
 * Desc:
 */
public class StringUtilsTest {
    public static void main(String[] args) throws Exception {
        List<String> strings  = new ArrayList<>();
        strings.add("1");
        strings.add("22");
        strings.add("333");
        strings.add("4444");
        strings.add("55555");
        System.out.println(StringUtils.join(strings, ","));
    }
}
