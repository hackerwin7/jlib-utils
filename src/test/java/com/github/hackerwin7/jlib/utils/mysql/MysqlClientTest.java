package com.github.hackerwin7.jlib.utils.mysql;

import com.github.hackerwin7.jlib.utils.drivers.mysql.data.MyData;
import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.drivers.mysql.client.MysqlClient;
import com.github.hackerwin7.jlib.utils.drivers.mysql.conf.MysqlConf;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/20
 * Time: 11:12 AM
 * Desc:
 */
public class MysqlClientTest {
    public static void main(String[] args) throws Exception {
        MysqlClientTest mct = new MysqlClientTest();
        //mct.descTest();
        mct.rdInsertTest();
    }

    public void descTest() throws Exception {
        MysqlConf conf = new MysqlConf();
        conf.setProp(MysqlConf.MYSQL_HOST, "192.168.144.116");
        conf.setProp(MysqlConf.MYSQL_USER, "canal");
        conf.setProp(MysqlConf.MYSQL_PASSWORD, "canal");
        conf.setProp(MysqlConf.MYSQL_DATABASE, "canal_test");
        MysqlClient client = new MysqlClient(conf);
        MyData desc = client.desc("orders_test");
        System.out.println(desc);
    }

    public void rdInsertTest() throws Exception {
        MysqlConf conf = new MysqlConf();
        conf.setProp(MysqlConf.MYSQL_HOST, "192.168.144.116");
        conf.setProp(MysqlConf.MYSQL_USER, "canal");
        conf.setProp(MysqlConf.MYSQL_PASSWORD, "canal");
        conf.setProp(MysqlConf.MYSQL_DATABASE, "canal_test");
        MysqlClient client = new MysqlClient(conf);
        client.rdInsert("orders_test", 100);
    }

    public void genSqlsTest() throws Exception {
        MyData data = new MyData();
        data.setTbname("test");
        MyData.Column column = MyData.Column.createBuilder()
                .name("uid")
                .javaType("java.lang.Integer")
                .build();
        data.setColumn(column);
        MyData.Column column1 = MyData.Column.createBuilder()
                .name("name")
                .javaType("java.lang.String")
                .build();
        data.setColumn(column1);
        MysqlClientTest mct = new MysqlClientTest();
        String sql = rdGenInsSql(data);
        System.out.println(sql);
    }

    private String rdGenInsSql(MyData desc) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(desc.getTbname()).append(" ");
        List<MyData.Column> columns = desc.getColumnList();
        List<String> names = new ArrayList<>();
        List<String> values = new ArrayList<>();
        Random random = new Random();
        for(MyData.Column column : columns) {
            /* skip the auto_increment and not allowed written filed */
            if(column.isAuto() || !column.isWrite())
                continue;
            names.add(column.getName());
            String value = null;
            switch (column.getJavaType()) {
                case "java.lang.Long":
                    value = String.valueOf(Math.abs(random.nextLong()) % 999999999999L);
                    break;
                case "java.lang.String":
                    value = "\'" + CommonUtils.randomString() + "\'";
                    break;
                case "java.lang.Integer":
                    value = String.valueOf(random.nextInt(9999));
                    break;
                case "java.sql.Timestamp":
                    value = "\'" +  String.valueOf(new java.sql.Date(System.currentTimeMillis())) + "\'";
                    break;
                case "java.math.BigDecimal":
                    value = String.valueOf(Math.abs(random.nextFloat()));
                    break;
                default:
                    value = "\'" + CommonUtils.randomString() + "\'";
            }
            values.add(value);
        }
        sql.append("(").append(StringUtils.join(names, ",")).append(")").append(" values ");
        sql.append("(").append(StringUtils.join(values, ",")).append(")");
        return sql.toString();
    }
}