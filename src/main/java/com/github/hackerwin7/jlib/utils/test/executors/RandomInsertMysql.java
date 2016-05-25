package com.github.hackerwin7.jlib.utils.test.executors;

import com.github.hackerwin7.jlib.utils.test.drivers.mysql.client.MysqlClient;
import com.github.hackerwin7.jlib.utils.test.drivers.mysql.conf.MysqlConf;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/20
 * Time: 3:24 PM
 * Desc: random insert mysql table
 */
public class RandomInsertMysql {

    /**
     * parameter is a string = host:port:user:password:database:table:count
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        RandomInsertMysql rim = new RandomInsertMysql();
        rim.run(args[0]);
    }

    public void run(String parameters) throws Exception {
        /* parameter parse */
        String[] paraArr = StringUtils.split(parameters, ":");
        String host = paraArr[0];
        String port = paraArr[1];
        String user = paraArr[2];
        String password = paraArr[3];
        String database = paraArr[4];
        String table = paraArr[5];
        int count = Integer.parseInt(paraArr[6]);

        /* build mysql client */
        MysqlConf conf = new MysqlConf();
        conf.setProp(MysqlConf.MYSQL_HOST, host);
        conf.setProp(MysqlConf.MYSQL_PORT, port);
        conf.setProp(MysqlConf.MYSQL_USER, user);
        conf.setProp(MysqlConf.MYSQL_PASSWORD, password);
        conf.setProp(MysqlConf.MYSQL_DATABASE, database);
        MysqlClient client = new MysqlClient(conf);
        client.rdInsert(table, count);
    }
}
