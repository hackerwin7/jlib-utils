package com.github.hackerwin7.jlib.utils.common;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        ct.test5();
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

    public void test2() {
        Long valL = 1L;
        Integer valI = 1;
        Object valLO = (Object) valL;
        Object valIO = (Object) valI;
        System.out.println(valLO.getClass());
        System.out.println(valIO.getClass());
    }

    public void test3() {
        Long valL = 1L;
        Integer valI = 1;
        Object valLO = (Object) valL;
        Object valIO = (Object) valI;
        System.out.println(Objects.equals(valLO, valIO));
        try {
//            System.out.println(Objects.equals(valLO, Class.forName(valLO.getClass().getName()).cast(valIO)));
//            System.out.println(Objects.equals(Class.forName(valIO.getClass().getName()).cast(valLO), valIO));
            System.out.println(StringUtils.equals( Objects.toString(valLO) , Objects.toString(valIO)));
        } catch (Exception e) {
            e.printStackTrace();
        }


        CommmonTest ct1 = new CommmonTest();
        CommmonTest ct2 = new CommmonTest();
        System.out.println(Objects.toString(ct1));
        System.out.println(Objects.toString(ct2));
    }

    public boolean retBool() {
        return true && (false && true);
    }

    public void test4() {
        System.out.println(retBool());
    }

    public void test5() {
        try {
            CommmonTest ct = (CommmonTest) CommonUtils.newInstanceOb("com.github.hackerwin7.jlib.utils.common.CommmonTest");
            System.out.println(ct.retBool());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
