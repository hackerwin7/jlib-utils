package com.github.hackerwin7.jlib.utils.mysql;

import com.github.hackerwin7.jlib.utils.drivers.mysql.client.MysqlClient;
import com.github.hackerwin7.jlib.utils.drivers.mysql.conf.MysqlConf;
import com.github.hackerwin7.jlib.utils.drivers.mysql.data.MyData;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/28
 * Time: 12:11 PM
 * Desc: mysql client test
 */
public class MysqlClientTest {
    public static void main(String[] args) throws Exception {
        MysqlConf conf = new MysqlConf();
        conf.setProp(MysqlConf.MYSQL_HOST, "192.168.144.116");
        conf.setProp(MysqlConf.MYSQL_PORT, "3306");
        conf.setProp(MysqlConf.MYSQL_DATABASE, "canal_test");
        conf.setProp(MysqlConf.MYSQL_USER, "canal");
        conf.setProp(MysqlConf.MYSQL_PASSWORD, "canal");
        MysqlClient client = new MysqlClient(conf);
        MyData data = client.desc("orders_test");
        System.out.println(data);
    }
}
