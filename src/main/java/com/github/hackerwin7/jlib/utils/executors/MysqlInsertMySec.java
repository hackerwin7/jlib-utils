package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.file.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/25
 * Time: 3:19 PM
 * Desc: insert my_sec test
 */
public class MysqlInsertMySec {

    private String host = "127.0.0.1";
    private int port = 3306;
    private String db = "canal_test";
    private String tb = "my_sec";
    private String usr = "mysql";
    private String psd = "mysql";

    private List<String> types = null;
    private Connection conn = null;
    private Random random = new Random();

    public static void main(String[] args) throws Exception {

    }

    public void mainProc(long cnt) throws Exception {
        long count = cnt;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, usr, psd);
        String sql = "insert into " + db + "(" +
                "name1," +
                "name2," +
                "name3," +
                "name4," +
                "name5," +
                "name6," +
                "name7," +
                "name8," +
                "name9," +
                "name10," +
                "name11," +
                "name12," +
                "name13," +
                "name14," +
                "name15," +
                "name16," +
                "name17," +
                "memberID," +
                "customerName," +
                "sendpay," +
                "address," +
                "Uprovince," +
                "ucity," +
                "ucounty," +
                "schoolId," +
                "schooluid," +
                "code," +
                "phone," +
                "usermob," +
                "email," +
                "payment," +
                "di," +
                "remark," +
                "createDate," +
                "state," +
                "state2," +
                "treatedDate," +
                "treatedsum," +
                "treatedRemark," +
                "yn," +
                "totalPrice," +
                "userremark," +
                "yun," +
                "usertruep," +
                "orderfid," +
                "orderfren," +
                "orderftime," +
                "orderftel," +
                "orderpuyn," +
                "paydate," +
                "outdate," +
                "paysuredate," +
                "resivedate," +
                "youhui," +
                "lastyu," +
                "newyu," +
                "trueyun," +
                "oprator," +
                "jifen," +
                "jyn," +
                "pyt," +
                "payremk," +
                "printtime," +
                "printx," +
                "premark," +
                "tidate," +
                "shdate," +
                "shdatesum," +
                "Cky," +
                "fhy," +
                "chuna," +
                "ztdate," +
                "ztyn," +
                "fp," +
                "ziti," +
                "zioper," +
                "zititime," +
                "dabaotime," +
                "autoremark," +
                "operren," +
                "operq," +
                "kt," +
                "hzbj," +
                "cky2," +
                "cky3," +
                "printy," +
                "ordtype," +
                "bankname," +
                "userorderid," +
                "storeid," +
                "parentid," +
                "splitType," +
                "updateDate," +
                "executor," +
                "ext0," +
                "ext1," +
                "ext2," +
                "ext3," +
                "townId," +
                ") " +
                "values (" +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                "?," +
                ")";
        types = FileUtils.file2List("row1.list");
        executeTimes(1000000, sql);
    }

    private void executeTimes(int count, String sql) throws Exception {
        PreparedStatement stmt = conn.prepareStatement(sql);
        long cur = System.currentTimeMillis();
        for(int i = 1; i <= 99; i++) {
            if(StringUtils.containsIgnoreCase(types.get(i - 1), "int")) {
                stmt.setInt(i, random.nextInt(10000));
            } else if(StringUtils.containsIgnoreCase(types.get(i - 1), "date")) {
                stmt.setDate(i, new Date(cur));
            } else {
                stmt.setString(i, "1qaz@WSX");
            }
        }
        stmt.execute();
    }
}
