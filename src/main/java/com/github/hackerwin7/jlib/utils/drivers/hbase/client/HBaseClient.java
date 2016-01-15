package com.github.hackerwin7.jlib.utils.drivers.hbase.client;

import com.github.hackerwin7.jlib.utils.commons.convert.Convert;
import com.github.hackerwin7.jlib.utils.drivers.hbase.conf.HBaseConf;
import com.github.hackerwin7.jlib.utils.drivers.hbase.data.HData;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/22
 * Time: 4:34 PM
 * Desc: hbase client for hbase-client 1.1.2
 */
public class HBaseClient {
    /*logger*/
    private static Logger logger = Logger.getLogger(HBaseClient.class);

    /*driver*/
    private Connection connection = null;
    private Admin admin = null;
    private Map<String, Table> tablePool = new HashMap<>();
    private Map<String, BufferedMutator> mutatorPool = new HashMap<>();
    private Map<String, BufferedMutator> asyncMutatorPool = new HashMap<>();

    /*timer*/
    private FlushTask ft = new FlushTask();

    /*constants*/
    public static final long TIMER_DELAY_SCHEDULE = 3 * 1000;
    public static final long TIMER_PERIOD_SCHEDULE = 10 * 1000;
    public static final int DEFAULT_SCAN_CACHING = 1000;
    public static final int LOOKAHEAD_SCAN_SEEK = 2;


    /**
     * init hbase conn and admin and table
     * @param hconf
     * @throws Exception
     */
    public HBaseClient(HBaseConf hconf) throws Exception {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HBaseConf.HBASE_ZK_QUORUM, hconf.getProp(HBaseConf.HBASE_ZK_QUORUM));
        configuration.set(HBaseConf.HBASE_ZK_PORT, hconf.getProp(HBaseConf.HBASE_ZK_PORT));
        configuration.set(HBaseConf.HBASE_ZK_NODE_ROOT, hconf.getProp(HBaseConf.HBASE_ZK_NODE_ROOT));
        connection = ConnectionFactory.createConnection(configuration);
        admin = connection.getAdmin();
        Timer timer = new Timer();
        timer.schedule(ft, TIMER_DELAY_SCHEDULE, TIMER_PERIOD_SCHEDULE);
    }

    /**
     * getOrigin table from pool
     * if not exists, create a table
     * @param tbName
     * @return table
     * @throws Exception
     */
    private Table getTable(String tbName) throws Exception {
        if(!tablePool.containsKey(tbName)) {
            if(!existsTable(tbName))
                createTable(tbName);
            Table table = connection.getTable(TableName.valueOf(tbName));
            tablePool.put(tbName, table);
        }
        return tablePool.get(tbName);
    }

    /**
     * getOrigin/create a mutator with table name, similar to Table
     * @param tbName
     * @return mutator
     * @throws Exception
     */
    private BufferedMutator getMutator(String tbName) throws Exception {
        if(!mutatorPool.containsKey(tbName)) {
            if(!existsTable(tbName))
                createTable(tbName);
            BufferedMutator mutator = connection.getBufferedMutator(TableName.valueOf(tbName));
            mutatorPool.put(tbName, mutator);
        }
        return mutatorPool.get(tbName);
    }

    /**
     * getOrigin/create a async mutator with table name, similar to Table and mutator
     * @param tbName
     * @return mutator
     * @throws Exception
     */
    private BufferedMutator getAsyncMutator(String tbName) throws Exception {
        if(!asyncMutatorPool.containsKey(tbName)) {
            if(!existsTable(tbName))
                createTable(tbName);
            BufferedMutator asyncMutator = connection.getBufferedMutator(TableName.valueOf(tbName));
            asyncMutatorPool.put(tbName, asyncMutator);
        }
        return asyncMutatorPool.get(tbName);
    }

    /************************ hbase admin operator ****************************/

    /**
     * htable is exists
     * @param tbName
     * @return bool
     * @throws Exception
     */
    public boolean existsTable(String tbName) throws Exception {
        return admin.tableExists(TableName.valueOf(tbName));
    }

    /**
     * create htable
     * @param tbName
     * @throws Exception
     */
    public void createTable(String tbName) throws Exception {
        if(existsTable(tbName)) {
            logger.warn("table " + tbName + " is exists!");
        } else {
            HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tbName));
            admin.createTable(descriptor);
        }
    }

    /**
     * create the hbase table with hbase descriptor
     * @param tbName
     * @param families
     * @throws Exception
     */
    public void createTable(String tbName, String[] families) throws Exception {
        if(existsTable(tbName)) {
            logger.warn("table " + tbName + " is exists!");
        } else {
            HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tbName));
            for(String family : families) {
                descriptor.addFamily(new HColumnDescriptor(family));
            }
            admin.createTable(descriptor);
        }
    }

    /**
     * getOrigin column families
     * @param tbName
     * @return
     * @throws Exception
     */
    public HColumnDescriptor[] getColumnFamilies(String tbName) throws Exception {
        return admin.getTableDescriptor(TableName.valueOf(tbName)).getColumnFamilies();
    }

    /**
     * getOrigin all column families name
     * @param tbName
     * @return families name
     * @throws Exception
     */
    public List<String> getColumnFamiliesName(String tbName) throws Exception {
        HTableDescriptor descriptor = admin.getTableDescriptor(TableName.valueOf(tbName));
        HColumnDescriptor[] columns = descriptor.getColumnFamilies();
        List<String> families = new ArrayList<>();
        for(HColumnDescriptor column : columns) {
            families.add(column.getNameAsString());
        }
        return families;
    }

    /**
     * add family columng
     * @param tbName
     * @param families
     * @throws Exception
     */
    public void addFamily(String tbName, String[] families) throws Exception {
        if(existsTable(tbName)) {
            List<String> getfams = getColumnFamiliesName(tbName);
            for(String addfm : families) {
                boolean isadd = true;
                for(String fmName : getfams) {
                    if(StringUtils.equals(addfm, fmName)) {
                        isadd = false;
                        break;
                    }
                }
                if(isadd)
                    admin.addColumn(TableName.valueOf(tbName), new HColumnDescriptor(addfm));
            }
        } else {
            logger.error("table is not exists!");
        }
    }

    /**
     * create namespace
     * @param namespace
     * @throws Exception
     */
    public void createNamespace(String namespace) throws Exception {
        admin.createNamespace(NamespaceDescriptor.create(namespace).build());
    }

    /**
     * delete family column
     * @param tbName
     * @param family
     * @throws Exception
     */
    public void deleteFamily(String tbName, String family) throws Exception {
        admin.deleteColumn(TableName.valueOf(tbName), Bytes.toBytes(family));
    }

    /**
     * delete namespace
     * @param namespace
     * @throws Exception
     */
    public void deleteNamespace(String namespace) throws Exception {
        admin.deleteNamespace(namespace);
    }

    /**
     * delete htable
     * @param tbName
     * @throws Exception
     */
    public void deleteTable(String tbName) throws Exception {
        if(existsTable(tbName)) {
            admin.disableTable(TableName.valueOf(tbName));
            admin.deleteTable(TableName.valueOf(tbName));
        }
    }

    /**
     * getOrigin table name list
     * @return name list
     * @throws Exception
     */
    public List<String> getTableNameList() throws Exception {
        TableName[] tableNames = admin.listTableNames();
        List<String> tns = new ArrayList<>();
        for(TableName tableName : tableNames) {
            tns.add(tableName.getNameAsString());
        }
        return tns;
    }

    /************************ hbase table operator ****************************/


    /**
     * origin put
     * @param tbName
     * @param put
     * @throws Exception
     */
    public void putOrigin(String tbName, Put put) throws Exception {
        Table table = getTable(tbName);
        table.put(put);
    }

    /**
     * origin puts
     * @param tbName
     * @param puts
     * @throws Exception
     */
    public void putOrigin(String tbName, List<Put> puts) throws Exception {
        Table table = getTable(tbName);
        table.put(puts);
    }

    /**
     * put single data
     * @param tbName
     * @param data
     * @throws Exception
     */
    public void put(String tbName, HData data) throws Exception {
        Table table = getTable(tbName);
        Put put = Convert.hdata2put(data);
        table.put(put);
    }

    /**
     * put list datas
     * @param tbName
     * @param datas
     * @throws Exception
     */
    public void put(String tbName, List<HData> datas) throws Exception {
        Table table = getTable(tbName);
        List<Put> puts = new ArrayList<>();
        for(HData data : datas) {
            Put put = Convert.hdata2put(data);
            puts.add(put);
        }
        table.put(puts);
    }

    /**
     * async mutate
     * @param tbName
     * @param put
     * @throws Exception
     */
    public void putAsyncOrigin(String tbName, Put put) throws Exception {
        BufferedMutator asyncMutator = getAsyncMutator(tbName);
        asyncMutator.mutate(put);
    }

    /**
     * async batch mutate
     * @param tbName
     * @param puts
     * @throws Exception
     */
    public void putAsyncOrigin(String tbName, List<Put> puts) throws Exception {
        BufferedMutator asyncMutator = getAsyncMutator(tbName);
        asyncMutator.mutate(puts);
    }

    /**
     * async put HData
     * @param tbName
     * @param data
     * @throws Exception
     */
    public void putAsync(String tbName, HData data) throws Exception {
        BufferedMutator asyncMutator = getAsyncMutator(tbName);
        Put put = Convert.hdata2put(data);
        asyncMutator.mutate(put);
    }

    /**
     * async put bulk data, but no flush
     * per minute or same interval flush the mutator
     * @param tbName
     * @param datas
     * @throws Exception
     */
    public void putAsync(String tbName, List<HData> datas) throws Exception {
        BufferedMutator asyncMutator = getAsyncMutator(tbName);
        List<Put> puts = new ArrayList<>();
        for(HData data : datas) {
            Put put = Convert.hdata2put(data);
            puts.add(put);
        }
        asyncMutator.mutate(puts);
    }

    /**
     * origonal getOrigin
     * @param tbName
     * @param get
     * @return hdata
     * @throws Exception
     */
    public HData getOrigin(String tbName, Get get) throws Exception {
        Table table = getTable(tbName);
        Result res = table.get(get);
        return Convert.result2hdata(res);
    }

    /**
     * batch
     * @param tbName
     * @param gets
     * @return list of hdata
     * @throws Exception
     */
    public List<HData> getOrigin(String tbName, List<Get> gets) throws Exception {
        Table table = getTable(tbName);
        Result[] results = table.get(gets);
        List<HData> hDatas = new ArrayList<>();
        for(Result result : results) {
            HData data = Convert.result2hdata(result);
            hDatas.add(data);
        }
        return hDatas;
    }

    /**
     * overloading
     * @param tbName
     * @param rowkey
     * @return hdata
     * @throws Exception
     */
    public HData get(String tbName, byte[] rowkey) throws Exception {
        return getOrigin(tbName, new Get(rowkey));
    }

    public List<HData> get(String tbName, List<byte[]> rows) throws Exception {
        List<Get> gets = new ArrayList<>();
        for(byte[] rowkey : rows) {
            Get get = new Get(rowkey);
            gets.add(get);
        }
        return getOrigin(tbName, gets);
    }

    /**
     * hdata parameter
     * @param tbName
     * @param data
     * @return hbase data
     * @throws Exception
     */
    public HData getHdata(String tbName, HData data) throws Exception {
        return get(tbName, data.getRowkey());
    }

    public List<HData> getHdata(String tbName, List<HData> datas) throws Exception {
        List<byte[]> rows = new ArrayList<>();
        for(HData data : datas) {
            rows.add(data.getRowkey());
        }
        return get(tbName, rows);
    }

    /**
     * exists getOrigin
     * @param tbName
     * @param get
     * @return bool
     * @throws Exception
     */
    public boolean existsRow(String tbName, Get get) throws Exception {
        Table table = getTable(tbName);
        return table.exists(get);
    }

    /**
     * overloading with rowkey
     * @param tbName
     * @param rowkey
     * @return bool
     * @throws Exception
     */
    public boolean existsRow(String tbName, byte[] rowkey) throws Exception {
        return existsRow(tbName, new Get(rowkey));
    }

    /**
     * delete the delte
     * @param tbName
     * @param delete
     * @throws Exception
     */
    public void deleteOrigin(String tbName, Delete delete) throws Exception {
        Table table = getTable(tbName);
        table.delete(delete);
    }

    public void deleteOrigin(String tbName, List<Delete> deletes) throws Exception {
        Table table = getTable(tbName);
        table.delete(deletes);
    }

    /**
     * delete by rowkey
     * @param tbName
     * @param rowkey
     * @throws Exception
     */
    public void delete(String tbName, byte[] rowkey) throws Exception {
        deleteOrigin(tbName, new Delete(rowkey));
    }

    public void delete(String tbName, List<byte[]> rows) throws Exception {
        List<Delete> deletes = new ArrayList<>();
        for(byte[] rowkey : rows) {
            deletes.add(new Delete(rowkey));
        }
        deleteOrigin(tbName, deletes);
    }

    /**
     * delete hdata
     * @param tbName
     * @throws Exception
     */
    public void deleteHData(String tbName, HData data) throws Exception {
        delete(tbName, data.getRowkey());
    }

    public void deleteHData(String tbName, List<HData> datas) throws Exception {
        List<byte[]> rows = new ArrayList<>();
        for(HData data : datas) {
            rows.add(data.getRowkey());
        }
        delete(tbName, rows);
    }

    /**
     * original delete async
     * @param tbName
     * @param deletes
     * @throws Exception
     */
    public void deleteAsyncOrigin(String tbName, List<Delete> deletes) throws Exception {
        BufferedMutator asyncMutator = getAsyncMutator(tbName);
        asyncMutator.mutate(deletes);
    }

    public void deleteAsync(String tbName, List<byte[]> rows) throws Exception {
        List<Delete> deletes = new ArrayList<>();
        for(byte[] rowkey : rows) {
            deletes.add(new Delete(rowkey));
        }
        deleteAsyncOrigin(tbName, deletes);
    }

    public void deleteAsyncHData(String tbName, List<HData> datas) throws Exception {
        List<byte[]> rows = new ArrayList<>();
        for(HData data : datas) {
            rows.add(data.getRowkey());
        }
        deleteAsync(tbName, rows);
    }

    /**
     * original scanner
     * @param tbName
     * @param scan
     * @return
     * @throws Exception
     */
    public List<HData> getsOrigin(String tbName, Scan scan) throws Exception {
        Table table = getTable(tbName);
        ResultScanner scanner = null;
        List<HData> datas = new ArrayList<>();
        try {
            scanner = table.getScanner(scan);
            for(Result res : scanner) {
                HData data = Convert.result2hdata(res);
                datas.add(data);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(scanner != null)
                scanner.close();
        }
        return datas;
    }

    /**
     * scan data by start rowkey and stop rowkey
     * @param tbName
     * @param start
     * @param stop
     * @return list of hdata
     * @throws Exception
     */
    public List<HData> gets(String tbName, byte[] start, byte[] stop) throws Exception {
        Scan scan = new Scan();
        scan.setCaching(DEFAULT_SCAN_CACHING);
        scan.setStartRow(start);
        scan.setStopRow(stop);
        return getsOrigin(tbName, scan);
    }

    public List<HData> gets(String tbName, byte[] start, byte[] stop, byte[] family) throws Exception {
        Scan scan = new Scan();
        scan.addFamily(family);
        scan.setCaching(DEFAULT_SCAN_CACHING);
        scan.setStartRow(start);
        scan.setStopRow(stop);
        return getsOrigin(tbName, scan);
    }

    public List<HData> gets(String tbName, byte[] start, byte[] stop, byte[] family, byte[] qualifier) throws Exception {
        Scan scan = new Scan();
        scan.addColumn(family, qualifier);
        scan.setCaching(DEFAULT_SCAN_CACHING);
        scan.setStartRow(start);
        scan.setStopRow(stop);
        return getsOrigin(tbName, scan);
    }

    /**
     * set start and load count hdatas to list
     * @param tbName
     * @param start
     * @param count
     * @param nextHandler
     * @return hdatas
     * @throws Exception
     */
    public List<HData> gets(String tbName, byte[] start, int count, NextRowkeyHandler nextHandler) throws Exception {
        byte[] stop = getStop(start, count, nextHandler);
        return gets(tbName, start, stop);
    }

    public List<HData> gets(String tbName, byte[] start, int count, NextRowkeyHandler nextHandler, byte[] family) throws Exception {
        byte[] stop = getStop(start, count, nextHandler);
        return gets(tbName, start, stop, family);
    }

    public List<HData> gets(String tbName, byte[] start, int count, NextRowkeyHandler nextHandler, byte[] family, byte[] qualifier) throws Exception {
        byte[] stop = getStop(start, count, nextHandler);
        return gets(tbName, start, stop, family, qualifier);
    }

    /**
     * getOrigin stop row key with next handler
     * @param start
     * @param cnt
     * @param handler
     * @return stop rowkey
     */
    private byte[] getStop(byte[] start, int cnt, NextRowkeyHandler handler) {
        byte[] cur = start;
        for(int i = 0; i <= cnt - 1; i++)
            cur = handler.next(cur);
        return cur;
    }

    /*next row key handler as method parameter*/
    public interface NextRowkeyHandler {
        /**
         * getOrigin next rowkey
         * @param cur
         * @return
         */
        public byte[] next(byte[] cur);
    }

    /*every interval time, flush the async mutator map*/
    public class FlushTask extends TimerTask {
        /*logger*/
        private Logger logger = Logger.getLogger(FlushTask.class);

        /**
         * flush mutators
         */
        public void run() {
            if(asyncMutatorPool != null) {
                for(Map.Entry<String, BufferedMutator> mutatorEntry : asyncMutatorPool.entrySet()) {
                    BufferedMutator mutator = mutatorEntry.getValue();
                    try {
                        mutator.flush();
                    } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                        //!!!!!!!!!!!!!!!!!!!!! add retry with this, if not then system exit
                    }
                }
            }
        }
    }

    /**
     * close table pool
     * @throws Exception
     */
    private void closeTablePool(Map<String, Table> tpool) throws Exception {
        if(tpool != null) {
            for (Map.Entry<String, Table> tableEntry : tpool.entrySet()) {
                String tableName = tableEntry.getKey();
                Table table = tableEntry.getValue();
                if (table != null)
                    table.close();
            }
        }
    }

    /**
     * close mutator pool
     * @param mpool
     * @throws Exception
     */
    private void closeMutatorPool(Map<String, BufferedMutator> mpool) throws Exception {
        if(mpool != null) {
            for(Map.Entry<String, BufferedMutator> mutatorEntry : mpool.entrySet()) {
                String tableName = mutatorEntry.getKey();
                BufferedMutator mutator = mutatorEntry.getValue();
                if(mutator != null) {
                    mutator.flush();//before the close flush the mutator
                    mutator.close();
                }
            }
        }
    }

    /**
     * close the connection
     * @throws Exception
     */
    public void close() throws Exception {
        if(tablePool != null) {
            closeTablePool(tablePool);
        }
        if(mutatorPool != null) {
            closeMutatorPool(mutatorPool);
        }
        if(admin != null) {
            admin.close();
        }
        if(connection != null) {
            connection.close();
        }
    }
}
