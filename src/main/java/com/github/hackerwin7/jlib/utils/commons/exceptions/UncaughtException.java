package com.github.hackerwin7.jlib.utils.commons.exceptions;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/06
 * Time: 5:16 PM
 * Desc:
 * Tips:
 */
public class UncaughtException implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = Logger.getLogger(UncaughtException.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOG.error("uncaught exception thread = " + t.getName() + ", error = " + e.getMessage(), e);
    }
}
