package com.github.hackerwin7.jlib.utils.executors;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/11/02
 * Time: 4:23 PM
 * Desc:
 * Tips:
 */
public class SystemProperty {

    private static final Logger LOG = Logger.getLogger(SystemProperty.class);

    public static void main(String[] args) {
        while (true) {

            LOG.info(System.getProperty("user.dir"));

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
