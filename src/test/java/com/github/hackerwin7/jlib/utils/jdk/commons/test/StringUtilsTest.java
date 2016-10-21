package com.github.hackerwin7.jlib.utils.jdk.commons.test;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
//        List<String> strings  = new ArrayList<>();
//        strings.add("1");
//        strings.add("22");
//        strings.add("333");splitByWholeSeparator
//        strings.add("4444");
//        strings.add("55555");
//        System.out.println(StringUtils.join(strings, ","));
//
//        String path = "/root/path/ins/";
//        String[] arr = StringUtils.split(path, "/");
//        for(String ar : arr) {
//            System.out.println(ar);
//        }

        StringUtilsTest sut = new StringUtilsTest();
        sut.t1();

    }

    public void t1() {
        String str = "111#222#333#  # # #     #";
        System.out.println(ArrayUtils.toString(StringUtils.split(str, "#")));
        System.out.println(ArrayUtils.toString(StringUtils.splitByWholeSeparator(str, "#")));
    }
}
