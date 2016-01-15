package com.github.hackerwin7.jlib.utils.drivers.mysql.client;

import com.github.hackerwin7.jlib.utils.drivers.mysql.conf.MysqlConf;
import com.github.hackerwin7.jlib.utils.drivers.mysql.data.MyData;
import com.github.hackerwin7.jlib.utils.drivers.queue.blocking.BlockingDataQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/24
 * Time: 2:20 PM
 * Desc: mysql jdbc client
 */
public class MysqlClient {
    /*constants*/
    public static final int QUEUE_DEFAULT_SIZE = 10000;

    /*logger*/
    private static Logger logger = Logger.getLogger(MysqlClient.class);

    /*data*/
    private String db = null;
    private int batchSize = 10000;

    /*driver*/
    private Connection conn = null;

    /*queue*/
    private BlockingDataQueue<MyData> queue = new BlockingDataQueue<>(QUEUE_DEFAULT_SIZE);

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

//    /**
//     * random insert with table name
//     * @param tb
//     * @param count
//     * @throws Exception
//     */
//    public void rdInsert(String tb, int count) throws Exception {
//        if(counts > batchSize) {
//            rdInsertAsync(tb, count);
//        } else {
//            rdInsertSync(tb, count);
//        }
//    }

    /**
     * query from sql , receive list MyData
     * @param sql
     * @return list of MyData
     * @throws Exception
     */
    public List<MyData> query(String sql) throws Exception {
        //get describe
        MyData descData = desc(getTbFromSql(sql));
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<MyData> datas = new ArrayList<>();
        while (rs.next()) {
            //res to data
            MyData data = getDataFromRes(rs, descData);
            datas.add(data);
        }
        rs.close();
        ps.close();
        return datas;
    }

    /**
     * query sql retrieve data into queue
     * @param sql
     * @throws Exception
     */
    public void queryAsync(final String sql) throws Exception {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyData descData = desc(getTbFromSql(sql));
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    List<MyData> datas = new ArrayList<>();
                    while (rs.next()) {
                        //res to data
                        MyData data = getDataFromRes(rs, descData);
                        queue.put(data);
                    }
                    rs.close();
                    ps.close();
                } catch (Throwable e) {
                    logger.error(e.getMessage());
                }
            }
        });
        th.start();
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
        MyData data = new MyData();
        for (int i = 1; i <= rsd.getColumnCount(); i++) {
            String jtype = rsd.getColumnClassName(i);
            String mtype = rsd.getColumnTypeName(i);
            int size = rsd.getColumnDisplaySize(i);
            String name = rsd.getColumnName(i);
            MyData.Column column = MyData.Column.createBuilder()
                    .name(name)
                    .value(null)
                    .javaType(jtype)
                    .sqlType(mtype)
                    .length(size)
                    .isNull(true)
                    .build();
            data.setColumn(column);
        }
        return data;
    }

    /**
     * get table name form sql
     * @param sql
     * @return table name
     * @throws Exception
     */
    private String getTbFromSql(String sql) throws Exception {
        if(StringUtils.containsIgnoreCase(sql, "from")) {
            String sub = StringUtils.substringAfter(sql, "from ");
            String table = StringUtils.substringBefore(sub, " ");
            return table;
        } else {
            throw new Exception("not found table name");
        }
    }

    /**
     * get mydata from result set
     * @param rs
     * @param desc
     * @return mydata
     */
    private MyData getDataFromRes(ResultSet rs, MyData desc) throws Exception {
        MyData data = new MyData();
        for(MyData.Column column : desc.getColumnList()) {
            MyData.Column vc = getColFromRes(rs, column);
            data.setColumn(vc);
        }
        return data;
    }

    private MyData.Column getColFromRes(ResultSet rs, MyData.Column descCol) throws Exception {
        String name = descCol.getName();
        String value = descCol.getValue();
        String jtype = descCol.getJavaType();
        String mtype = descCol.getSqlType();
        int length = descCol.getLength();
        switch (jtype) {
            case "java.lang.Long":
                value = String.valueOf(rs.getLong(name));
                break;
            case "java.lang.String":
                value = rs.getString(name);
                break;
            case "java.lang.Integer":
                value = String.valueOf(rs.getInt(name));
                break;
            case "java.sql.Timestamp":
                value = String.valueOf(rs.getTimestamp(name));
                break;
            case "java.math.BigDecimal":
                value = String.valueOf(rs.getBigDecimal(name));
                break;
            default:
                value = String.valueOf(rs.getObject(name));
        }
        MyData.Column column = MyData.Column.createBuilder()
                .name(name)
                .value(value)
                .javaType(jtype)
                .sqlType(mtype)
                .length(length)
                .isNull(false)
                .build();
        return column;
    }

    /**
     * set batch size
     * @param batchSize
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public MyData getMyData() throws Exception {
        return queue.take();
    }

    public boolean isDataQueueEmpty() {
        return queue.isEmpty();
    }
}
