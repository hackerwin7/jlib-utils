package com.github.hackerwin7.jlib.utils.file;

import org.codehaus.plexus.util.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/17
 * Time: 1:34 PM
 * Desc:
 * Tips:
 */
public class FileModeTest {
    public static void main(String[] args) {
        String str = "abc";
        String del = "coco;";
        System.out.println(StringUtils.leftPad(str, 13, del));
    }
}
