package com.github.hackerwin7.jlib.utils.test.executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/15
 * Time: 9:00 PM
 * Desc:
 */
public class HBaseClientScanIns {

    public static Logger logger = Logger.getLogger(HBaseClientScanIns.class);

    public static Configuration configuration;
    public static Connection connection;
    public static Admin admin;

    public static void main(String[] args) throws Exception {
        init();
        scanData(args[0]);
    }

    public static void init() throws Exception {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "172.19.186.89,172.19.186.90,172.19.186.91,172.19.186.93,172.19.186.93");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("zookeeper.znode.parent", "/hbase_paris");
        connection = ConnectionFactory.createConnection(configuration);
        admin = connection.getAdmin();
    }

    public static void close() throws Exception {
        if(admin != null)
            admin.close();
        if(connection != null)
            connection.close();
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

    public static void showCell(Result result) throws Exception {
        Cell[] cells = result.rawCells();
        for(Cell cell : cells) {
            logger.info("row name = " + String.valueOf(Bytes.toLong(CellUtil.cloneRow(cell))) + " ");
            logger.info("timestamp = " + cell.getTimestamp() + " ");
            logger.info("family = " + new String(CellUtil.cloneFamily(cell)) + " ");
            logger.info("column = " + new String(CellUtil.cloneQualifier(cell)) + " ");
            logger.info("value = " + new String(CellUtil.cloneValue(cell)) + " ");
        }
    }

    public static void scanData(String tableName) throws Exception {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        ResultScanner resultScanner = table.getScanner(scan);
        for(Result result : resultScanner) {
            showCell(result);
        }
        table.close();
        close();
    }
}
