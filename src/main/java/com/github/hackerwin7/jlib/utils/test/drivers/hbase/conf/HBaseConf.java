package com.github.hackerwin7.jlib.utils.test.drivers.hbase.conf;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/23
 * Time: 11:05 AM
 * Desc: hbase config for hbase client init
 */
public class HBaseConf {
    /*data*/
    private Properties props = new Properties();

    /*constants*/
    public static final String HBASE_ZK_QUORUM = "hbase.zookeeper.quorum";
    public static final String HBASE_ZK_PORT = "hbase.zookeeper.property.clientPort";
    public static final String HBASE_ZK_NODE_ROOT = "zookeeper.znode.parent";

    /**
     * constructor default
     */
    public HBaseConf() {
        props.put(HBASE_ZK_QUORUM, "127.0.0.1");
        props.put(HBASE_ZK_PORT, "2181");
        props.put(HBASE_ZK_NODE_ROOT, "/hbase");
    }

    /**
     * set key, value properties
     * @param key
     * @param val
     */
    public void setProp(String key, String val) {
        props.setProperty(key, val);
    }

    /**
     * getOrigin key value property
     * @param key
     * @return prop value
     */
    public String getProp(String key) {
        return props.getProperty(key);
    }
}
