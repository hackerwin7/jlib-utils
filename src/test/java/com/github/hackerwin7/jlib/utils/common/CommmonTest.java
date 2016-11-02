package com.github.hackerwin7.jlib.utils.common;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/31
 * Time: 3:05 PM
 * Desc:
 * Tips:
 */
public class CommmonTest {
    public static void main(String[] args) {
        CommmonTest ct = new CommmonTest();
        ct.test1();
    }

    public void test() {
        while (true) {

            System.out.println(CommonUtils.getPid());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void test1() {
        System.out.println(System.getProperty("user.dir"));
    }
}
