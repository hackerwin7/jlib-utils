package com.github.hackerwin7.jlib.utils.jdk.commons.test;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/19
 * Time: 5:09 PM
 * Desc: random test next number
 */
public class RandomTest {
    public static void main(String[] args) throws Exception {
        Random random = new Random();
        for(int i = 1; i <= 100; i++) {
            int n = random.nextInt();
            int n1 = random.nextInt(100);
            System.out.println(n + " " + n1);
        }
    }
}
