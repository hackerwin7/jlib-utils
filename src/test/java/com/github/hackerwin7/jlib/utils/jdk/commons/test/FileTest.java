package com.github.hackerwin7.jlib.utils.jdk.commons.test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/10
 * Time: 12:38 PM
 * Desc:
 * Tips:
 */
public class FileTest {
    public static void main(String[] args) throws Exception {
        FileTest ft = new FileTest();
        ft.test1();
    }

    public void test1() throws Exception {
        List<String> val = Files.readAllLines(new File("/home/fff/Servers/mars-admin-1.1-SNAPSHOT/logs/mars.pid").toPath());
        System.out.print(val);
    }
}
