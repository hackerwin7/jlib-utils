package com.github.hackerwin7.jlib.utils.test.drivers.mysql.client;

import com.github.hackerwin7.jlib.utils.test.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.test.drivers.mysql.conf.MysqlConf;
import com.github.hackerwin7.jlib.utils.test.drivers.mysql.data.MyData;
import com.github.hackerwin7.jlib.utils.test.drivers.queue.blocking.BlockingDataQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public static final long RANDOM_LONG_RANGE = 999999999;
    public static final int RANDOM_INT_RANGE = 9999;


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
    public void execute(String sql) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error("error occurred when closing the statement, error = " + e.getMessage(), e);
                }
        }
    }

    /**
     * execute batch
     * @param sqls
     * @throws Exception
     */
    public void executeBatch(List<String> sqls) {
        Statement stmt = null;
        try {
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            int index = 0;
            for(String sql : sqls) {
                index++;
                stmt.addBatch(sql);
                if(index % batchSize == 0) {
                    logger.info("executed " + batchSize + " sqls......");
                    stmt.executeBatch();
                    conn.commit();
                }
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            //close
            try {
                if(stmt != null)
                    stmt.close();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("error occurred when closing the batch statement, error = " + e.getMessage(), e);
            }
        }

    }

    /**
     * random insert with table name
     * @param tb
     * @param count
     * @throws Exception
     */
    public void rdInsert(String tb, int count) throws Exception {
        if(count > batchSize) {
            rdInsertAsync(tb, count);
        } else {
            rdInsertSync(tb, count);
        }
    }

    /**
     * async insert
     * @param tbName
     * @param count
     * @throws Exception
     */
    private void rdInsertAsync(String tbName, int count) throws Exception {
        /* get table desc */
        MyData desc = desc(tbName);
        /* generate sql and execute */
        List<String> sqlList = new ArrayList<>();
        String sql = rdGenInsSql(desc);
        for(int i = 1; i <= count; i++) {
            sqlList.add(sql);
        }
        executeBatch(sqlList);
    }

    /**
     * sync insert
     * @throws Exception
     */
    private void rdInsertSync(String tbName, int count) throws Exception {
        /* get table desc */
        MyData desc = desc(tbName);
        /* generate sql and execute */
        for(int i = 1; i <= count; i++) {
            String sql = rdGenInsSql(desc);
            execute(sql);
        }
    }

    /**
     * generate sql by description data
     * @param desc
     * @return string sql
     * @throws Exception
     */
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
                    value = String.valueOf(Math.abs(random.nextLong()) % RANDOM_LONG_RANGE);
                    break;
                case "java.lang.String":
                    value = "\'" + CommonUtils.randomString() + "\'";
                    break;
                case "java.lang.Integer":
                    value = String.valueOf(random.nextInt(RANDOM_INT_RANGE));
                    break;
                case "java.sql.Timestamp":
                    value = "\'" +  String.valueOf(new Date(System.currentTimeMillis())) + "\'";
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
        data.setTbname(tbName);
        for (int i = 1; i <= rsd.getColumnCount(); i++) {
            String jtype = rsd.getColumnClassName(i);
            String mtype = rsd.getColumnTypeName(i);
            int size = rsd.getColumnDisplaySize(i);
            String name = rsd.getColumnName(i);
            boolean isAuto = rsd.isAutoIncrement(i);
            boolean isWrite = rsd.isWritable(i);
            MyData.Column column = MyData.Column.createBuilder()
                    .name(name)
                    .value(null)
                    .javaType(jtype)
                    .sqlType(mtype)
                    .length(size)
                    .isNull(true)
                    .isAuto(isAuto)
                    .isWrite(isWrite)
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
