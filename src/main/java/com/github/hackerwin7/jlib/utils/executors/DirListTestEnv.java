package com.github.hackerwin7.jlib.utils.executors;

import java.io.File;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/06
 * Time: 3:53 PM
 * Desc:
 * Tips:
 */
public class DirListTestEnv {
    public static void main(String[] args) {
        File home = new File(System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.dir"));
        File workingDir = home.getParentFile();
        File[] files = workingDir.listFiles();
        System.out.println(files.length);
        System.out.println(Arrays.toString(files));
    }
}
