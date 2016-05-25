package com.github.hackerwin7.jlib.utils.test.executors;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/04/22
 * Time: 3:00 PM
 * Desc:
 * Tips:
 */
public class JSample {
    public static void main(String[] args) throws Exception {
        while (true) {
            Thread.sleep(2000);
            System.out.println(new Date());
        }
    }
}
