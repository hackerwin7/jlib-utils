package com.github.hackerwin7.jlib.utils.mysql;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/25
 * Time: 2:23 PM
 * Desc:
 */
public class MysqlTest {
    private static String ip = "192.168.144.116";
    private static String db = "canal_test";
    private static String tb = "orders_test";
    private static String usr = "canal";
    private static String psd = "canal";

    public static String url = "jdbc:mysql://" + ip + "/" + db;

    public static final String JDBC = "com.mysql.jdbc.Driver";

    public static void main(String[] args) throws Exception {
        MysqlTest mt = new MysqlTest();
        mt.mainProc2();
    }

    public void mainProc() throws Exception {
        Class.forName(JDBC);
        Connection conn = DriverManager.getConnection(url, usr, psd);
        PreparedStatement stmt = conn.prepareStatement("insert into " + tb + " (name) values (?)");
        stmt.setString(1, "df0");
        boolean rs = stmt.execute();
        System.out.println(rs);
        stmt.close();
        conn.close();
    }

    public void mainProc1() throws Exception {
        Class.forName(JDBC);
        Connection conn = DriverManager.getConnection(url, usr, psd);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tb);
        while (rs.next()) {
            String col = rs.getString("name");
            System.out.println("row = " + rs.getRow());
            System.out.println(col);
        }
        rs.close();
        stmt.close();
        conn.close();
    }

    public void mainProc2() throws Exception {
        Class.forName(JDBC);
        Connection conn = DriverManager.getConnection(url, usr, psd);
        PreparedStatement stmt = conn.prepareStatement("select * from " + tb + " limit 1");
        ResultSetMetaData rsd = stmt.executeQuery().getMetaData();
        for(int i = 1; i <= rsd.getColumnCount(); i++) {
            String jtype = rsd.getColumnClassName(i);
            String mtype = rsd.getColumnTypeName(i);
            int size = rsd.getColumnDisplaySize(i);
            String name = rsd.getColumnName(i);
            System.out.println("name = " + name + ", jtype = " + jtype + ", mtype = " + mtype + ", size = " + size);
        }
    }
}
