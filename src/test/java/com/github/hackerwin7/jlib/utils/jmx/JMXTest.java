package com.github.hackerwin7.jlib.utils.jmx;

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
        jt.JMXClientGaugeTest1();
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
}
