package com.github.hackerwin7.jlib.utils.drivers.mysql.client;

import com.github.hackerwin7.jlib.utils.drivers.mysql.conf.MysqlConf;
import com.github.hackerwin7.jlib.utils.drivers.mysql.data.MyColumn;
import com.github.hackerwin7.jlib.utils.drivers.mysql.data.MyData;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/24
 * Time: 2:20 PM
 * Desc: mysql jdbc client
 */
public class MysqlClient {
    /*logger*/
    private static Logger logger = Logger.getLogger(MysqlClient.class);

    /*data*/
    private String db = null;
    private int batchSize = 10000;

    /*driver*/
    private Connection conn = null;

    /**
     * connect the mysql server using jdbc
     * @param conf
     * @throws Exception
     */
    public MysqlClient(MysqlConf conf) throws Exception {
        String host = conf.getProp(MysqlConf.MYSQL_HOST);
        int port = Integer.valueOf(conf.getProp(MysqlConf.MYSQL_PORT));
        db = conf.getProp(MysqlConf.MYSQL_DATABASE);
        String usr = conf.getProp(MysqlConf.MYSQL_USER);
        String psd = conf.getProp(MysqlConf.MYSQL_PASSWORD);
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, usr, psd);
    }

    /**
     * execute update delete or insert
     * @param sql
     * @throws Exception
     */
    public void execute(String sql) throws Exception {
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();
        stmt.close();
    }

    /**
     * execute batch
     * @param sqls
     * @throws Exception
     */
    public void executeBatch(List<String> sqls) throws Exception {
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        for(int i = 0; i <= sqls.size() - 1; i++) {
            int index = i + 1;
            if(index % batchSize == 0) {
                stmt.executeBatch();
                conn.commit();
            }
        }
        stmt.executeBatch();
        conn.commit();
        //close
        stmt.close();
        conn.setAutoCommit(true);
    }

    /**
     * queryData from sql , receive list MyData
     * @param sql
     * @return list of MyData
     * @throws Exception
     */
    public List<MyData> queryData(String sql) throws Exception {
        //describe table
        String tbName = tableFromSql(sql);
        MyData desc = desc(tbName);
        Map<String, MyColumn> columns = desc.getColumns();
        //select table
        List<MyData> datas = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            MyData data = MyData.create()
                    .tbname(tbName)
                    .dbname(db)
                    .build();
            for(Map.Entry<String, MyColumn> columnEntry : columns.entrySet()) {
                MyColumn column = columnEntry.getValue();
                String name = column.getName();
                String jtype = column.getJtype();
                data.addCol(
                    MyColumn.create()
                        .name(name)
                        .jtype(jtype)
                        .value(getValue(res, name, jtype))
                        .build()
                );
            }
            datas.add(data);
        }
        res.close();
        stmt.close();
        return datas;
    }

    /**
     * query result set
     * @param sql
     * @return result set
     * @throws Exception
     */
    public ResultState queryResult(String sql) throws Exception {
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        return new ResultState(stmt, rs);
    }

    /**
     * desc table structure
     * @param tbName
     * @return MyData
     * @throws Exception
     */
    public MyData desc(String tbName) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("select * from " + tbName + " limit 1");
        ResultSetMetaData rsd = stmt.executeQuery().getMetaData();
        MyData data = MyData.create()
                .dbname(db)
                .tbname(tbName)
                .build();
        for (int i = 0; i <= rsd.getColumnCount() - 1; i++) {
            String jtype = rsd.getColumnClassName(i + 1);
            String mtype = rsd.getColumnTypeName(i + 1);
            int size = rsd.getColumnDisplaySize(i + 1);
            String name = rsd.getColumnName(i + 1);
            data.addCol(MyColumn.create()
            .jtype(jtype)
            .mtype(mtype)
            .size(size)
            .name(name)
            .build());
        }
        return data;
    }

    /**
     * get table name form sql
     * @param sql
     * @return table name
     * @throws Exception
     */
    private String tableFromSql(String sql) throws Exception {
        if(StringUtils.containsIgnoreCase(sql, "from")) {
            String sub = StringUtils.substringAfter(sql, "from ");
            String table = StringUtils.substringBefore(sub, " ");
            return table;
        } else {
            throw new Exception("not found table name");
        }
    }

    /**
     * get mtype and result set
     * @param rs
     * @param type
     * @return string value
     */
    private String getValue(ResultSet rs, String name, String type) throws Exception {
        if(StringUtils.containsIgnoreCase(type, "string")) {
            return rs.getString(name);
        } else if(StringUtils.containsIgnoreCase(type, "bigint")) {
            return String.valueOf(rs.getBigDecimal(name));
        } else if(StringUtils.containsIgnoreCase(type, "int")) {
            return String.valueOf(rs.getInt(name));
        } else if(StringUtils.containsIgnoreCase(type, "bool")) {
            return String.valueOf(rs.getBoolean(name));
        } else if(StringUtils.containsIgnoreCase(type, "date")) {
            return String.valueOf(rs.getDate(name));
        } else if(StringUtils.containsIgnoreCase(type, "double")) {
            return String.valueOf(rs.getDouble(name));
        } else if(StringUtils.containsIgnoreCase(type, "long")) {
            return String.valueOf(rs.getLong(name));
        } else if(StringUtils.containsIgnoreCase(type, "timestamp")) {
            return String.valueOf(rs.getTimestamp(name));
        }
        else {
            throw new Exception("no support exception with name = " + name + ", mtype = " + type);
        }
    }

    public class ResultState {
        private ResultSet rs = null;
        private PreparedStatement stmt = null;

        public ResultState(PreparedStatement stmt, ResultSet rs) {
            this.stmt = stmt;
            this.rs = rs;
        }

        public void close() throws Exception {
            if(rs != null) {
                rs.close();
            }
            if(stmt != null) {
                stmt.close();
            }
        }

        public ResultSet getRs() {
            return rs;
        }

        public PreparedStatement getStmt() {
            return stmt;
        }
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

}
