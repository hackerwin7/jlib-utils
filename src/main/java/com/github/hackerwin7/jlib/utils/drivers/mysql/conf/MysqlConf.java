package com.github.hackerwin7.jlib.utils.drivers.mysql.conf;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/25
 * Time: 3:01 PM
 * Desc: mysql config
 */
public class MysqlConf {
    /*data*/
    private Properties props = new Properties();

    /*constats*/
    public static final String MYSQL_HOST = "mysql.host";
    public static final String MYSQL_PORT = "mysql.port";
    public static final String MYSQL_USER = "mysql.user";
    public static final String MYSQL_PASSWORD = "mysql.password";
    public static final String MYSQL_DATABASE = "mysql.database";

    /**
     * default config
     */
    public MysqlConf() {
        props.setProperty(MYSQL_HOST, "127.0.0.1");
        props.setProperty(MYSQL_PORT, "3306");
        props.setProperty(MYSQL_USER, "mysql");
        props.setProperty(MYSQL_PASSWORD, "mysql");
        props.setProperty(MYSQL_DATABASE, "test");
    }

    /**
     * set properties key value
     * @param key
     * @param val
     */
    public void setProp(String key, String val) {
        props.setProperty(key, val);
    }

    /**
     * get property value
     * @param key
     * @return
     */
    public String getProp(String key) {
        return props.getProperty(key);
    }
}
