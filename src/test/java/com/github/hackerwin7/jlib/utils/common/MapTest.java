package com.github.hackerwin7.jlib.utils.common;

import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2017/01/17
 * Time: 4:25 PM
 * Desc:
 * Tips:
 */
public class MapTest {
    public static void main(String[] args) throws Exception {
        MapTest mt = new MapTest();
        mt.test();
    }

    public void test() {
        Map<String, Object> map = new HashedMap();
        map.put("ok", "okkkkk");

        String kk = (String) map.get("faewfafe");
        System.out.println(kk);
    }
}
