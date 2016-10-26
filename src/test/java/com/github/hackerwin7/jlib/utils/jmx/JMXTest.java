package com.github.hackerwin7.jlib.utils.jmx;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
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
        jt.readTest();
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
}
