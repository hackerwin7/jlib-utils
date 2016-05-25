package com.github.hackerwin7.jlib.utils.test.hbase.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/08
 * Time: 10:36 AM
 * Desc: hbase client test
 */
public class HBaseClientTest {

    private static Logger logger = Logger.getLogger(HBaseClientTest.class);

    public static Configuration configuration;
    public static Connection connection;
    public static Admin admin;

    public static void main(String[] args) throws Exception {

    }

    public static void init() throws Exception {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "127.0.0.1");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("zookeeper.znode.parent", "/hbase");
        connection = ConnectionFactory.createConnection(configuration);
        admin = connection.getAdmin();
    }

    public static void close() throws Exception {
        if(admin != null)
            admin.close();
        if(connection != null)
            connection.close();
    }

    public static void createTable(String tbName, String[] cols) throws Exception {
        init();
        TableName tableName = TableName.valueOf(tbName);
        if(admin.tableExists(tableName))
            logger.warn("table is exists!");
        else {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
            for(String col : cols) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);
                hTableDescriptor.addFamily(hColumnDescriptor);
            }
            admin.createTable(hTableDescriptor);
        }
        close();
    }

    public static void deleteTable(String tbName) throws Exception {
        init();
        TableName tn = TableName.valueOf(tbName);
        if(admin.tableExists(tn)) {
            admin.disableTable(tn);
            admin.deleteTable(tn);
        }
        close();
    }

    public static void listTable() throws Exception {
        init();
        HTableDescriptor hTableDescriptors[] = admin.listTables();
        for(HTableDescriptor hTableDescriptor : hTableDescriptors) {
            logger.info(hTableDescriptor.getNameAsString());
        }
        close();
    }

    public static void insertRow(String tableName, String rowkey, String colFamily, String col, String val) throws Exception {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(val));
        table.put(put);
//        /*batch insert*/
//        List<Put> putList = new ArrayList<Put>();
//        putList.add(put);
//        table.put(putList);
        table.close();
        close();
    }

    public static void deleteRow(String tableName, String rowkey, String colFamily, String col) throws Exception {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
//        //delete family
//        delete.addFamily(Bytes.toBytes(colFamily));
//        //delete column
//        delete.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
        table.delete(delete);
//        //batch delete
//        List<Delete> deleteList = new ArrayList<Delete>();
//        deleteList.add(delete);
//        table.delete(deleteList);
        table.close();
        close();
    }

    public static void getData(String tableName, String rowkey, String colFamily, String col) throws Exception {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowkey));
        //getOrigin family
        get.addFamily(Bytes.toBytes(colFamily));
        //getOrigin col
        get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
        Result result = table.get(get);
        showCell(result);
        table.close();
        close();
    }

    public static void showCell(Result result) throws Exception {
        Cell[] cells = result.rawCells();
        for(Cell cell : cells) {
            logger.info("row name = " + new String(CellUtil.cloneRow(cell)) + " ");
            logger.info("timestamp = " + cell.getTimestamp() + " ");
            logger.info("family = " + new String(CellUtil.cloneFamily(cell)) + " ");
            logger.info("column = " + new String(CellUtil.cloneQualifier(cell)) + " ");
            logger.info("value = " + new String(CellUtil.cloneValue(cell)) + " ");
        }
    }

    public static void scanData(String tableName, String startRow, String stopRow) throws Exception {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow));
        ResultScanner resultScanner = table.getScanner(scan);
        for(Result result : resultScanner) {
            showCell(result);
        }
        table.close();
        close();
    }
}
