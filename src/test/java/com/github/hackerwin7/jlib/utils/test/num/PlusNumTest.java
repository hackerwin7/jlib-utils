package com.github.hackerwin7.jlib.utils.test.num;

import com.github.hackerwin7.jlib.utils.test.drivers.file.FileUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/02/22
 * Time: 2:51 PM
 * Desc:
 */
public class PlusNumTest {
    public static void main(String args[]) throws Exception {
        List<String> numList = FileUtils.file2List("num.list");
        for(String numStr : numList) {
            int num = Integer.parseInt(numStr) + 1;
            System.out.println(num);
        }
    }
}
