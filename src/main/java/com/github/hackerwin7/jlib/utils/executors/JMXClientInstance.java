package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.jmx.JMXClient;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/07
 * Time: 2:29 PM
 * Desc:
 * Tips:
 */
public class JMXClientInstance {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String obName = args[2];
        String attName = args[3];
        JMXClient client = new JMXClient(host, port);
        System.out.println("get " + obName + "& " + attName + " = " + client.getAttribute(obName, attName));
        client.close();
    }
}
