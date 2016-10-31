package com.github.hackerwin7.jlib.utils.executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/31
 * Time: 10:33 AM
 * Desc: a test for process builder and get the process id
 * Tips:
 */
public class ProcessBuilderId {

    private static final Logger LOG = Logger.getLogger(ProcessBuilderId.class);

    public static void main(String[] args) {
        while (true) {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String[] nArr = StringUtils.split(name, "@");
            LOG.info(name + " running process id " + nArr[0] + " ...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
