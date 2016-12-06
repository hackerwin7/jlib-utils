package com.github.hackerwin7.jlib.utils.log4j;

import com.github.hackerwin7.jlib.utils.commons.exceptions.UncaughtException;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/06
 * Time: 5:02 PM
 * Desc:
 * Tips:
 */
public class UncaughtExceptionTest {

    public static final Logger LOG = Logger.getLogger(UncaughtExceptionTest.class);

    public static void main(String[] args) {

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtException());

        UncaughtExceptionTest uet = new UncaughtExceptionTest();
        uet.test1();
    }

    public void test1() {
        LOG.info("starting ......");

        File file = null;
        file.getAbsolutePath();
    }
}
