package com.github.hackerwin7.jlib.utils.file;

import com.github.hackerwin7.jlib.utils.drivers.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
        ft.test10();
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
        FileUtils.unTarGz(new File("/home/fff/Projects/mars/mars-dist/mars-admin-bin/target/mars-admin-1.0-SNAPSHOT.tar.gz"), new File("/home/fff/Servers"));
//        FileUtils.unTarGz(new File("/home/fff/Projects/mars/mars-dist/mars-admin-bin/target/mars-admin-1.0-SNAPSHOT.tar.gz"), new File("/home/fff/Servers"), "mars_mars");
    }

    public void test5() {
        System.out.println(Files.isSymbolicLink(Paths.get("/home/fff/Servers/mars")));
        System.out.println(Files.isSymbolicLink(Paths.get("/home/fff/Servers/mars-1.0-SNAPSHOT")));
        System.out.println(Files.isSymbolicLink(Paths.get("/home/fff/Servers/mars-1.0-SNAPSHOTsssss")));
    }

    public void test6() {
        System.out.println(System.getenv());
    }

    public void test7() {
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://localhost:8080/jars/mars-core-1.0-SNAPSHOT.jar"),
                    new File("/home/fff/Servers/mars" + "/lib/" + "mars-core-1.0-SNAPSHOT.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test8() {
        File file1 = new File("/home/fff/Servers");
        File file2 = new File("/home/fff/Servers/mars");
        File file3 = new File("/home/fff/Servers/mars-1.0-SNAPSHOT");
        File file4 = new File("/home/fff/Servers/dfaefa");
        File file5 = new File("/home/fff/Tmp/kk1");

        try {
            System.out.println(file1.getAbsolutePath() + ", " + file1.getCanonicalPath());
            System.out.println(file2.getAbsolutePath() + ", " + file2.getCanonicalPath());
            System.out.println(file3.getAbsolutePath() + ", " + file3.getCanonicalPath());
            System.out.println(file4.getAbsolutePath() + ", " + file4.getCanonicalPath());
            System.out.println(file5.getAbsolutePath() + ", " + file5.getCanonicalPath());

            if(!file5.exists())
                file5.mkdirs();
            System.out.println(file5.getAbsolutePath() + ", " + file5.getCanonicalPath() + ", " + file5.getName() + ", " + file5.exists());
            file5.renameTo(new File("/home/fff/Tmp/kk1r"));
            System.out.println(file5.getAbsolutePath() + ", " + file5.getCanonicalPath() + ", " + file5.getName() + ", " + file5.exists());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test9() {
        File file = new File("/home/fff/Servers/mars-admin");
        System.out.println(file.getAbsoluteFile().getName());
        try {
            System.out.println(file.getCanonicalFile().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test10() {
//        File file1 = new File("/home/fff/Servers/doctor-1.0-SNAPSHOT/target");
//        File file2 = new File("/home/fff/Servers/doctor-1.0-SNAPSHOT/conf");
//        org.apache.commons.io.FileUtils.deleteQuietly(file1);
//        try {
//            Files.delete(file2.toPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        File file = new File("/home/fff/Servers/doctor");
        file.delete();
    }
}
