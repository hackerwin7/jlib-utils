package com.github.hackerwin7.jlib.utils.jdk.commons.test;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/15
 * Time: 8:48 PM
 * Desc:
 */
public class StringTest {
    public static void main(String[] args) throws Exception {
        String str = "1000174,1000176,1000178,1000180,1000182,1000184,1000186,1000188,1000190,1000192,1000194,1000196,1000198,1000200,1000202,1000204,1000206,1000208,1000210,1000212";
        String[] strArr = StringUtils.split(str, ",");
        for(String s : strArr) {
            //System.out.println(Integer.valueOf(s) + 1);
            System.out.println("tp-" + s + ".jrdw.jd.com");
        }
    }
}
