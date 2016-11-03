package com.github.hackerwin7.jlib.utils.common;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;

import java.util.HashMap;
import java.util.Map;

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
        ct.mapCompTest();
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

    public void mapCompTest() {
        Map m1 = new HashMap();
        m1.put(true, true);
        m1.put("s1", "s2");

        Map m2= new HashMap();
        m2.put(true, true);
        m2.put("dd", false);

        System.out.println(m1.equals(m2));
    }
}
