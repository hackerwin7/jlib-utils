package com.github.hackerwin7.jlib.utils.executors;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/17
 * Time: 2:05 PM
 * Desc:
 */
public class StringGenerator {
    public static void main(String[] args) {
        String sku = "sku_";
        List<String> tbs = new LinkedList<>();
        for(int i = 1; i <= 64; i++) {
            tbs.add(sku + i);
        }
        System.out.println(StringUtils.join(tbs, ","));
    }
}
