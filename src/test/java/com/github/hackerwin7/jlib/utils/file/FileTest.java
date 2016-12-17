package com.github.hackerwin7.jlib.utils.file;

import com.github.hackerwin7.jlib.utils.drivers.file.FileUtils;

import java.io.File;
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
        ft.test4();
    }

    public void test1() {
        try {
            Files.createSymbolicLink(Paths.get("/home/fff/Servers/fire"), Paths.get("/home/fff/Servers/lein"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test2() {
        File file = new File("/home/fff/Servers/mars-admin-1.0-SNAPSHOT");
        File file1 = new File("/home/fff/Servers/mars-admin");
        try {
            System.out.println(file.getAbsoluteFile().getName() + ", " + file.getCanonicalFile().getName());
            System.out.println(file1.getAbsoluteFile().getName() + ", " + file1.getCanonicalFile().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void test3() {
        FileUtils.unTarGzPlexus(new File("/home/fff/Projects/mars/mars-dist/mars-admin-bin/target/mars-admin-1.0-SNAPSHOT.tar.gz"), new File("/home/fff/Servers"));
    }

    public void test4() {
//        FileUtils.unTarGz(new File("/home/fff/Projects/mars/mars-dist/mars-admin-bin/target/mars-admin-1.0-SNAPSHOT.tar.gz"), new File("/home/fff/Servers"));
        FileUtils.unTarGz(new File("/home/fff/Projects/mars/mars-dist/mars-admin-bin/target/mars-admin-1.0-SNAPSHOT.tar.gz"), new File("/home/fff/Servers"), "mars_mars");
    }
}
