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
    private static String ip = "127.0.0.1";
    private static String db = "test";
    private static String tb = "test";
    private static String usr = "mysql";
    private static String psd = "mysql";

    public static String url = "jdbc:mysql://" + ip + "/" + db;

    public static final String JDBC = "com.mysql.jdbc.Driver";

    public static void main(String[] args) throws Exception {

    }

    public void mainProc() throws Exception {
        Class.forName(JDBC);
        Connection conn = DriverManager.getConnection(url, usr, psd);
        PreparedStatement stmt = conn.prepareStatement("insert into " + tb + " (name) values (?)");
        stmt.setString(1, "uuiidd");
        boolean rs = stmt.execute();
        System.out.print(rs);
        stmt.close();
        conn.close();
    }

    public void mainProc1() throws Exception {
        Class.forName(JDBC);
        Connection conn = DriverManager.getConnection(url, usr, psd);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tb);
        while (rs.next()) {
            int col = rs.getInt("clumnName");
        }
        rs.close();
        stmt.close();
        conn.close();
    }
}
