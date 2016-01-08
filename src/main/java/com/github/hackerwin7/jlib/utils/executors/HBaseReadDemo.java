package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.drivers.hbase.conf.HBaseConf;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/30
 * Time: 5:37 PM
 * Desc: read base from rowkeu
 */
public class HBaseReadDemo {

    /**
     *
     * @param args zk:port:zkroot:tbname:family:rowkey
     */
    public static void main(String[] args) throws Exception {
        String hbstr = args[0];
        String[] hbs = StringUtils.split(hbstr, ":");
        String hbZk = hbs[0];
        int hbPort = Integer.valueOf(hbs[1]);
        String hbRoot = hbs[2];
        String hbTb = hbs[3];
        String fm = hbs[4];
        String rowkey = hbs[5];

        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HBaseConf.HBASE_ZK_QUORUM, hbZk);
        configuration.set(HBaseConf.HBASE_ZK_PORT, String.valueOf(hbPort));
        configuration.set(HBaseConf.HBASE_ZK_NODE_ROOT, hbRoot);
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
        Table table = connection.getTable(TableName.valueOf(hbTb));
        byte[] md5row = getMd5Row(rowkey);
        Get get = new Get(md5row);
        System.out.println(CommonUtils.showBytes(md5row));
        Result rs = table.get(get);
        for (Cell cell : rs.rawCells()) {
            StringBuilder sb = new StringBuilder();
            sb.append("row = ").append(Bytes.toString(CellUtil.cloneRow(cell))).append(",")
                    .append("family = ").append(Bytes.toString(CellUtil.cloneFamily(cell))).append(",")
                    .append("qualifier = ").append(Bytes.toString(CellUtil.cloneQualifier(cell))).append(",")
                    .append("value = ").append(Bytes.toString(CellUtil.cloneValue(cell)));
            System.out.println(sb.toString());
        }
    }

    public static byte[] getMd5Row(String rowkey) {
        int rowInt = Integer.valueOf(rowkey);
        byte[] rowBytes = Bytes.toBytes(rowInt);
        return Bytes.toBytes(MD5Hash.getMD5AsHex(rowBytes));
    }
}