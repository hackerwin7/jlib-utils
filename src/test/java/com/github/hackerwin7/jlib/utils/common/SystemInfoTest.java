package com.github.hackerwin7.jlib.utils.common;

import com.github.hackerwin7.jlib.utils.drivers.sys.PhysicalCores;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2017/01/16
 * Time: 4:41 PM
 * Desc:
 * Tips:
 */
public class SystemInfoTest {
    public static void main(String[] args) {
        SystemInfoTest sit = new SystemInfoTest();
        sit.test();
    }

    public void test() {
        System.out.println(Runtime.getRuntime().maxMemory());
        System.out.println(Runtime.getRuntime().totalMemory());

        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        System.out.println(os.getTotalPhysicalMemorySize());

        System.out.println(Runtime.getRuntime().availableProcessors());

        System.out.println(PhysicalCores.physicalCoreCount());
    }
}
