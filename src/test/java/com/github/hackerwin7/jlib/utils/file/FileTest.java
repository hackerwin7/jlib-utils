package com.github.hackerwin7.jlib.utils.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/12
 * Time: 5:38 PM
 * Desc:
 * Tips:
 */
public class FileTest {
    public static void main(String[] args) {
        FileTest ft = new FileTest();
        ft.test1();
    }

    public void test1() {
        try {
            Files.createSymbolicLink(Paths.get("/home/fff/Servers/fire"), Paths.get("/home/fff/Servers/lein"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
