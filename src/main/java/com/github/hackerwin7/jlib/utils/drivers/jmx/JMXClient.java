package com.github.hackerwin7.jlib.utils.drivers.jmx;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/26
 * Time: 4:50 PM
 * Desc: a simple jmx client for reading attributes
 * Tips:
 */
public class JMXClient {

    /* logger */
    private static final Logger LOG = Logger.getLogger(JMXClient.class);

    /* driver */
    private JMXConnector jmxc = null;
    private MBeanServerConnection mbsc = null;

    /**
     * string constructor a JMX client
     * @param url a string format for JMXServiceUrl
     */
    public JMXClient(String url) {
        prepare(url);
    }

    /**
     * overload constructor
     * @param host  null specify local
     * @param port  jmx remote port
     */
    public JMXClient(String host, int port) {
        if(StringUtils.isBlank(host))
            host = "";
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
        LOG.debug(url);
        prepare(url);
    }

    /**
     * prepare for JMXClient's driver
     * @param jmxUrl
     */
    private void prepare(String jmxUrl) {
        try {
            JMXServiceURL url = new JMXServiceURL(jmxUrl);
            jmxc = JMXConnectorFactory.connect(url, null);
            mbsc = jmxc.getMBeanServerConnection();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new JMXException(e.getMessage());
        }
    }

    /**
     * get attribute value
     * @param obName object name of JMX
     * @param attName attribute name of JMX object name
     * @return raw type object, null specify not exist or some exceptions happened
     */
    public Object getAttribute(ObjectName obName, String attName) {
        Object object = null;
        try {
            object = mbsc.getAttribute(obName, attName);
        } catch (IOException | MBeanException | AttributeNotFoundException |
                InstanceNotFoundException | ReflectionException e) {
            LOG.error(e.getMessage(), e);
        }
        return object;
    }

    /**
     * overload ObjectName args
     * @param name    the ObjectName of name
     * @param attName       attribute name
     * @return raw type
     */
    public Object getAttribute(String name, String attName) {
        Object object = null;
        try {
            ObjectName objectName = new ObjectName(name);
            object = getAttribute(objectName, attName);
        } catch (MalformedObjectNameException e) {
            LOG.error(e.getMessage(), e);
        }
        return object;
    }

    /**
     * close the jmx driver
     */
    public void close() {
        if(jmxc != null)
            try {
                jmxc.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw new JMXException(e.getMessage());
            }
    }
}
