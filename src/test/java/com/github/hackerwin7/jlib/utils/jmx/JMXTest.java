package com.github.hackerwin7.jlib.utils.jmx;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.drivers.jmx.JMXClient;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/26
 * Time: 4:22 PM
 * Desc:
 * Tips:
 */
public class JMXTest {
    public static void main(String[] args) {
        JMXTest jt = new JMXTest();
        jt.jmxTestMultiple();
    }

    public void readTest() {
        try {
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName objectName = new ObjectName("metrics:name=requests");
            Long count = (Long) mbsc.getAttribute(objectName, "Count");
            Double meanRate = (Double) mbsc.getAttribute(objectName, "MeanRate");
            System.out.println(count);
            System.out.println(meanRate);
        } catch (IOException | MalformedObjectNameException |
                MBeanException | AttributeNotFoundException |
                InstanceNotFoundException | ReflectionException e) {
            e.printStackTrace();
        }

    }

    public void JMXClientTest() {
        JMXClient jmx = new JMXClient(null, 9999);
        Long count = (Long) jmx.getAttribute("metrics:name=requests", "Count");
        Double mean = (Double) jmx.getAttribute("metrics:name=requests", "MeanRate");
        System.out.println(count + "\n" + mean);
    }

    public void JMXClientGaugeTest() {
        JMXClient jmx = new JMXClient(null, 9999);
        Boolean health = (Boolean) jmx.getAttribute("metrics:name=com.github.hackerwin7.jlib.utils.metrics.MetricsTest.gauge.test", "Value");
        System.out.println(health);
    }

    public void JMXClientGaugeTest1() {
        JMXClient jmx = new JMXClient(null, 9999);
        HashMap map = (HashMap) jmx.getAttribute("metrics:name=com.github.hackerwin7.jlib.utils.metrics.MetricsTest.gauge.test", "Value");
        System.out.println(map);
    }

    public void JMXClientGaugeTest2() throws Exception {
        JMXClient jmx = new JMXClient(null, 9999);
        HashMap map = (HashMap) jmx.getAttribute("metrics:name=fuck.gauge.test", "Value");
        System.out.println(map);
        while (true) {
            Thread.sleep(2000);
            System.out.println("jmx client waiting ....");
        }
    }

    public void jmxTestEnv() {
        JMXClient jmx = new JMXClient("172.16.115.95", 9999);
        Integer val = (Integer) jmx.getAttribute("metrics:name=\"supervisor:num-slots-used-gauge\"", "Value");
        System.out.println(val);
    }

    public void jmxTestDuring() {
        JMXClient jmx = new JMXClient(null, 9999);
        while (true) {
            try {
                HashMap map = (HashMap) jmx.getAttribute("metrics:name=continue", "Value");
                System.out.println(map);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                CommonUtils.delay(1000);
            }
        }
    }

    public void jmxTestMultiple() {
        JMXClient jmx = new JMXClient(null, 9999);
        int times = 1;
        while (times <= 10) {
            try {
                HashMap map = (HashMap) jmx.getAttribute("metrics:name=status", "Value");
                System.out.println(map);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                CommonUtils.delay(1000);
            }
            times++;
        }
        System.out.println("get the server metrics: " + jmx.getAttribute("metrics:name=server", "Value"));
        System.out.println("get the status metrics once again: " + jmx.getAttribute("metrics:name=status", "Value"));
    }
}
