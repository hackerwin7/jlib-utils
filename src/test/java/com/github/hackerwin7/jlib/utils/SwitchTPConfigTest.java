package com.github.hackerwin7.jlib.utils;

import com.github.hackerwin7.jlib.utils.executors.SwitchTpConfig;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/03/02
 * Time: 10:38 AM
 * Desc: test class SwitchTPConfig
 * Tips:
 */
public class SwitchTPConfigTest {
    public static void main(String[] args) throws Exception {
        System.out.println(SwitchTpConfig.getLogicName("orders_pop"));
        System.out.println(SwitchTpConfig.getLogicName("orders_pop_123"));
        System.out.println(SwitchTpConfig.getLogicName("orders_123"));
    }
}
