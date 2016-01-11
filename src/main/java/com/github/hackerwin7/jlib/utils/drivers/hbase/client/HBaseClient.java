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
     * get table from pool
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
     * get/create a mutator with table name, similar to Table
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
     * get/create a async mutator with table name, similar to Table and mutator
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
     * get column families
     * @param tbName
     * @return
     * @throws Exception
     */
    public HColumnDescriptor[] getColumnFamilies(String tbName) throws Exception {
        return admin.getTableDescriptor(TableName.valueOf(tbName)).getColumnFamilies();
    }

    /**
     * get all column families name
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
     * get table name list
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
     * filter mode save into hdata
     * @param tbName
     * @param data
     * @return hdatas
     * @throws Exception
     */
    public List<HData> get(String tbName, HData data) throws Exception {
        Table table = getTable(tbName);
        Get get = new Get(data.getRowkey());
        if(data.getFamily() != null && data.getQualifier() != null) {
            get.addColumn(data.getFamily(), data.getQualifier());
        } else if(data.getFamily() != null && data.getQualifier() == null) {
            get.addFamily(data.getFamily());
        }
        Result res = table.get(get);
        List<HData> datas = new ArrayList<>();
        for (Cell cell : res.rawCells()) {
            datas.add(HData.create()
                    .rowkey(cell.getRowArray())
                    .family(cell.getFamilyArray())
                    .qualifier(cell.getQualifierArray())
                    .value(cell.getValueArray())
                    .tablename(tbName)
                    .build());
        }
        return datas;
    }

    public List<HData> get(String tbName, List<HData> datas) throws Exception {
        Table table = getTable(tbName);
        List<Get> gets = new ArrayList<>();
        for(HData data : datas) {
            Get get = new Get(data.getRowkey());
            if(data.getFamily() != null && data.getQualifier() != null) {
                get.addColumn(data.getFamily(), data.getQualifier());
            } else if(data.getFamily() != null && data.getQualifier() == null) {
                get.addFamily(data.getFamily());
            }
            gets.add(get);
        }
        Result[] ress = table.get(gets);
        List<HData> hds = new ArrayList<>();
        for(Result res : ress) {
            for (Cell cell : res.rawCells()) {
                hds.add(HData.create()
                        .rowkey(cell.getRowArray())
                        .family(cell.getFamilyArray())
                        .qualifier(cell.getQualifierArray())
                        .value(cell.getValueArray())
                        .tablename(tbName)
                        .build());
            }
        }
        return hds;
    }

    /**
     * hdata contain filter condition
     * @param tbName
     * @param data
     * @return
     * @throws Exception
     */
    public boolean existsRow(String tbName, HData data) throws Exception {
        Table table = getTable(tbName);
        Get get = new Get(data.getRowkey());
        if(data.getFamily() != null && data.getQualifier() != null) {
            get.addColumn(data.getFamily(), data.getQualifier());
        } else if(data.getFamily() != null && data.getQualifier() == null) {
            get.addFamily(data.getFamily());
        }
        return table.exists(get);
    }

    /**
     * hdata contain row family qualifier
     * @param tbName
     * @param data
     * @throws Exception
     */
    public void delete(String tbName, HData data) throws Exception {
        Table table = getTable(tbName);
        Delete delete = new Delete(data.getRowkey());
        if(data.getFamily() != null && data.getQualifier() != null) {
            delete.addColumn(data.getFamily(), data.getQualifier());
        } else if(data.getFamily() != null && data.getQualifier() == null) {
            delete.addFamily(data.getFamily());
        }
        table.delete(delete);
    }

    /**
     * simple batch deletes
     * @param tbName
     * @param datas
     * @throws Exception
     */
    public void delete(String tbName, List<HData> datas) throws Exception {
        Table table = getTable(tbName);
        List<Delete> deletes = new ArrayList<>();
        for(HData data : datas) {
            Delete delete = new Delete(data.getRowkey());
            if(data.getFamily() != null && data.getQualifier() != null) {
                delete.addColumn(data.getFamily(), data.getQualifier());
            } else if(data.getFamily() != null && data.getQualifier() == null) {
                delete.addFamily(data.getFamily());
            }
            deletes.add(delete);
        }
        table.delete(deletes);
    }

    /**
     * delete bulk data
     * @param tbName
     * @param datas
     * @throws Exception
     */
    public void deleteBulk(String tbName, List<HData> datas) throws Exception {
        BufferedMutator mutator = getMutator(tbName);
        List<Delete> deletes = new ArrayList<>();
        for(HData data : datas) {
            Delete delete = new Delete(data.getRowkey());
            if(data.getFamily() != null && data.getQualifier() != null) {
                delete.addColumn(data.getFamily(), data.getQualifier());
            } else if(data.getFamily() != null && data.getQualifier() == null) {
                delete.addFamily(data.getFamily());
            }
            deletes.add(delete);
        }
        mutator.mutate(deletes);
        mutator.flush();
    }

    /**
     * async delete no flush at once
     * @param tbName
     * @param datas
     * @throws Exception
     */
    public void deleteAsync(String tbName, List<HData> datas) throws Exception {
        BufferedMutator asyncMutator = getAsyncMutator(tbName);
        List<Delete> deletes = new ArrayList<>();
        for(HData data : datas) {
            Delete delete = new Delete(data.getRowkey());
            if(data.getFamily() != null && data.getQualifier() != null) {
                delete.addColumn(data.getFamily(), data.getQualifier());
            } else if(data.getFamily() != null && data.getQualifier() == null) {
                delete.addFamily(data.getFamily());
            }
            deletes.add(delete);
        }
        asyncMutator.mutate(deletes);
    }

    /**
     * hdata filter condition
     * @param tbName
     * @param data
     * @return hdata list
     * @throws Exception
     */
    public List<HData> gets(String tbName, HData data) throws Exception {
        Table table = getTable(tbName);
        Scan scan = new Scan();
        if(data.getFamily() != null && data.getQualifier() != null) {
            scan.addColumn(data.getFamily(), data.getQualifier());
        } else if(data.getFamily() != null && data.getQualifier() == null) {
            scan.addFamily(data.getFamily());
        }
        ResultScanner scanner = table.getScanner(scan);
        List<HData> datas = new ArrayList<>();
        for(Result res : scanner) {
            for(Cell cell : res.rawCells()) {
                datas.add(HData.create()
                        .rowkey(cell.getRowArray())
                        .family(cell.getFamilyArray())
                        .qualifier(cell.getQualifierArray())
                        .value(cell.getValueArray())
                        .tablename(tbName)
                        .build());
            }
        }
        scanner.close();
        return datas;
    }

    /**
     * origin hbase scan interface
     * @param tbName
     * @param scan
     * @return hdata list
     * @throws Exception
     */
    private List<HData> gets(String tbName, Scan scan) throws Exception {
        Table table = getTable(tbName);
        ResultScanner scanner = table.getScanner(scan);
        List<HData> datas = new ArrayList<>();
        for(Result res : scanner) {
            for(Cell cell : res.rawCells()) {
                datas.add(HData.create()
                        .rowkey(cell.getRowArray())
                        .family(cell.getFamilyArray())
                        .qualifier(cell.getQualifierArray())
                        .value(cell.getValueArray())
                        .tablename(tbName)
                        .build());
            }
        }
        scanner.close();
        return datas;
    }

    /**
     * scan from start to stop - 1
     * @param tbName
     * @param filter
     * @param start
     * @param stop
     * @return hdatas
     * @throws Exception
     */
    public List<HData> gets(String tbName, HData filter, byte[] start, byte[] stop) throws Exception {
        Scan scan = new Scan();
        scan.setCaching(DEFAULT_SCAN_CACHING);
        filterScan(filter, scan);
        scan.setStartRow(start);
        scan.setStopRow(stop);
        return gets(tbName, scan);
    }

    /**
     * set start and load count hdatas to list
     * @param tbName
     * @param filter
     * @param start
     * @param count
     * @param nextHandler
     * @return hdatas
     * @throws Exception
     */
    public List<HData> gets(String tbName, HData filter, byte[] start, int count, NextRowkeyHandler nextHandler) throws Exception {
        byte[] stop = getStop(start, count, nextHandler);
        return gets(tbName, filter, start, stop);
    }

    /**
     * get stop row key with next handler
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
         * get next rowkey
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
     * add column or add family
     * @param filter
     * @param scan
     * @throws Exception
     */
    private void filterScan(HData filter, Scan scan) throws Exception {
        if(filter.getFamily() != null && filter.getQualifier() != null) {
            scan.addColumn(filter.getFamily(), filter.getQualifier());
        } else if(filter.getFamily() != null && filter.getQualifier() == null) {
            scan.addFamily(filter.getFamily());
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
                if(mutator != null)
                    mutator.close();
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
