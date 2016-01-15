package com.github.hackerwin7.jlib.utils.executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.apache.log4j.Logger;

import java.sql.*;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/29
 * Time: 3:21 PM
 * Desc: a demo (client is bad, rewrite it)
 */
public class Mysql2HBaseDemo {
    /*logger*/
    private Logger logger = Logger.getLogger(Mysql2HBaseDemo.class);

    /*data*/
    private String strMyConf = null;
    private String strHbConf = null;

    /*driver*/
    private Connection myConn = null;
    private org.apache.hadoop.hbase.client.Connection hbConn = null;
    private Admin admin = null;

    /*thread*/
    private Thread myth = null;
    private Thread hbth = null;

    /*queue*/
    private BlockingQueue<Put> queue = new LinkedBlockingQueue<>(QUEUE_LENGTH);

    /*constants*/
    public static final long SLEEP_INTERVAL = 5 * 1000;
    public static final long SLEEP_SHORT_INTERVAL = 500;
    public static final int QUEUE_LENGTH = 10000;
    public static final int BATCH_SIZE = 10000;
    public static final long SEND_INTERVAL = 3 * 1000;
    public static final long TIMER_DELAY = 3 * 1000;
    public static final long TIMER_PERIOD = 30 * 1000;
    public static final String PRIMARY_INPUT_CONN = ",";
    public static final String PRIMARY_ROWKEY_CONN = ":";

    /**
     * construct with string
     *
     * @param strMyConf host:port:user:password:dbname:tbname:primarykey
     * @param strHbConf zk:port:zkroot:tbname:family
     */
    public Mysql2HBaseDemo(String strMyConf, String strHbConf) {
        this.strMyConf = strMyConf;
        this.strHbConf = strHbConf;
    }

    /**
     * args have 2 parameters
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Mysql2HBaseDemo demo = new Mysql2HBaseDemo(args[0], args[1]);
        demo.start();
    }

    /**
     * start main process
     */
    public void start() throws Exception {
        while (true) {
            try {
                prepare();
                Thread.sleep(SLEEP_INTERVAL);
                while (true) {
                    run();
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                close();
            }
        }
    }

    /**
     * prepare conf
     *
     * @throws Exception
     */
    private void prepare() throws Exception {
        String[] mys = StringUtils.split(strMyConf, ":");
        String[] hbs = StringUtils.split(strHbConf, ":");
        //mysql load
        String myHost = mys[0];
        int myPort = Integer.valueOf(mys[1]);
        String myUser = mys[2];
        String myPassword = mys[3];
        String myDb = mys[4];
        final String myTb = mys[5];
        final String pri = mys[6];
        //hbase load
        String hbZk = hbs[0];
        int hbPort = Integer.valueOf(hbs[1]);
        String hbRoot = hbs[2];
        final String hbTb = hbs[3];
        final String fm = hbs[4];
        //show load conf
        logger.info("load mysql conf :");
        logger.info("---> host = " + myHost);
        logger.info("---> port = " + myPort);
        logger.info("---> user = " + myUser);
        logger.info("---> password = " + myPassword);
        logger.info("---> database = " + myDb);
        logger.info("---> table = " + myTb);
        logger.info("---> primary = " + pri);
        logger.info("load hbase conf :");
        logger.info("---> zk = " + hbZk);
        logger.info("---> port = " + hbZk);
        logger.info("---> root = " + hbRoot);
        logger.info("---> table = " + hbTb);
        logger.info("---> family = " + fm);
        //build mysql driver
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        myConn = DriverManager.getConnection("jdbc:mysql://" + myHost + ":" + myPort + "/" + myDb, myUser, myPassword);
        //build hbase driver
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", hbZk);
        configuration.set("hbase.zookeeper.property.clientPort", String.valueOf(hbPort));
        configuration.set("zookeeper.znode.parent", hbRoot);
        hbConn = ConnectionFactory.createConnection(configuration);
        admin = hbConn.getAdmin();
        if(admin == null) {
            logger.error("getOrigin admin failed!!");
            throw new Exception("getOrigin admin unsuccessfully...");
        }
        //thread nysql running
        myth = new Thread(new Runnable() {

            private byte[] getFieldBytes(ResultSet rs, String fieldName, String type) throws Exception {
                String ret = null;
                if (StringUtils.containsIgnoreCase(type, "string")) {
                    ret = rs.getString(fieldName);
                } else if(StringUtils.containsIgnoreCase(type, "long")) {
                    ret =  String.valueOf(rs.getLong(fieldName));
                } else if (StringUtils.containsIgnoreCase(type, "int")) {
                    ret = String.valueOf(rs.getInt(fieldName));
                } else if (StringUtils.containsIgnoreCase(type, "timestamp")) {
                    ret = String.valueOf(rs.getTimestamp(fieldName));
                } else if(StringUtils.containsIgnoreCase(type, "decimal")) {
                    ret = String.valueOf(rs.getBigDecimal(fieldName));
                }
                return Bytes.toBytes(ret);
            }

            private Map<String, DescMeta> getDesc(String tbName) throws Exception {
                PreparedStatement stmt = myConn.prepareStatement("select * from " + tbName + " limit 1");
                ResultSetMetaData rsd = stmt.executeQuery().getMetaData();
                Map<String, DescMeta> dms = new HashMap<>();
                for(int i = 0; i <= rsd.getColumnCount() - 1; i++) {
                    String jtype = rsd.getColumnClassName(i + 1);
                    String mtype = rsd.getColumnTypeName(i + 1);
                    int size = rsd.getColumnDisplaySize(i + 1);
                    String name = rsd.getColumnName(i + 1);
                    DescMeta dm = new DescMeta();
                    dm.setJtype(jtype);
                    dm.setMtype(mtype);
                    dm.setSize(size);
                    dm.setName(name);
                    dms.put(name, dm);
                }
                return dms;
            }

            private byte[] getRow(ResultSet rs, String fieldName, String type) throws Exception {
                byte[] origin = null;
                if (StringUtils.containsIgnoreCase(type, "string")) {
                    origin = Bytes.toBytes(rs.getString(fieldName));
                } else if(StringUtils.containsIgnoreCase(type, "long")) {
                    origin = Bytes.toBytes(rs.getLong(fieldName));
                } else if (StringUtils.containsIgnoreCase(type, "int")) {
                    origin = Bytes.toBytes(rs.getInt(fieldName));
                } else if (StringUtils.containsIgnoreCase(type, "timestamp")) {
                    origin = Bytes.toBytes(String.valueOf(rs.getTimestamp(fieldName)));
                } else if(StringUtils.containsIgnoreCase(type, "decimal")) {
                    origin = Bytes.toBytes(rs.getBigDecimal(fieldName));
                } else {
                    throw new Exception("not supported type!!!! , type = " + type);
                }
                String hashRow = MD5Hash.getMD5AsHex(origin);
                return Bytes.toBytes(hashRow);
            }

            private byte[] getRows(ResultSet rs, String filedNames, Map<String, DescMeta> metaMap) {
                return null;//to be continued
            }

            @Override
            public void run() {
                try {
                    //load file field name
                    Map<String, DescMeta> metas = getDesc(myTb);
                    logger.info("describe table = " + metas);
                    PreparedStatement stmt = myConn.prepareStatement("select * from " + myTb);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        //row key
                        byte[] rowkey = getRow(rs, pri, metas.get(pri).getJtype());
                        Put put = new Put(rowkey);//!!!!!!!!!!!!!!!!!!!!!! MD5 rowkey
                        for(Map.Entry<String, DescMeta> descEntry : metas.entrySet()) {
                            String name = descEntry.getKey();
                            DescMeta meta = descEntry.getValue();
                            put.addColumn(Bytes.toBytes(fm), Bytes.toBytes(name), getFieldBytes(rs, name, meta.getJtype()));
                        }
                        queue.put(put);
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        myth.start();
        //thread hbase running
        hbth = new Thread(new Runnable() {
            private BufferedMutator mutator = null;
            private Timer timer = new Timer();

            @Override
            public void run() {
                try {
                    //init hbase table
                    if(!admin.tableExists(TableName.valueOf(hbTb))) {
                        HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(hbTb));
                        descriptor.addFamily(new HColumnDescriptor(fm));
                        admin.createTable(descriptor);
                    }
                    mutator = hbConn.getBufferedMutator(TableName.valueOf(hbTb));
                    timer.schedule(new FlushTask(mutator), TIMER_DELAY, TIMER_PERIOD);
                    while (true) {
                        List<Put> puts = new LinkedList<>();
                        long start = System.currentTimeMillis();
                        boolean isLeap = true;
                        while (!queue.isEmpty()) {
                            isLeap = false;
                            Put put = queue.take();
                            puts.add(put);
                            if(puts.size() >= BATCH_SIZE || System.currentTimeMillis() - start >= SEND_INTERVAL) {
                                break;
                            }
                        }
                        //send put into hbase
                        mutator.mutate(puts);
                        //avoid running too fast
                        if(isLeap) {
                            Thread.sleep(SLEEP_SHORT_INTERVAL);
                        }
                        //end the hbase put schedule
                        if(!myth.isAlive() && queue.isEmpty()) {
                            break;
                        }
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        hbth.start();
    }

    /**
     * check the thread and driver's status
     * @throws Exception
     */
    public void run() throws Exception {

        if(!myth.isAlive() && !hbth.isAlive()) {
            close();
        }
        Thread.sleep(SLEEP_INTERVAL);
    }

    /**
     * close
     * @throws Exception
     */
    public void close() throws Exception {
        logger.info("exiting the system");
        System.exit(0);
    }

    public class DescMeta {
        public void setName(String name) {
            this.name = name;
        }

        public void setJtype(String jtype) {
            this.jtype = jtype;
        }

        public void setMtype(String mtype) {
            this.mtype = mtype;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public String getJtype() {
            return jtype;
        }

        public String getMtype() {
            return mtype;
        }

        public int getSize() {
            return size;
        }

        private String name = null;
        private String jtype = null;
        private String mtype = null;
        private int size = 0;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("name : ").append(name).append("|")
                    .append("jtype : ").append(jtype).append("|")
                    .append("mtype : ").append(mtype).append("|")
                    .append("size : ").append(size);
            return sb.toString();
        }

    }

    public class FlushTask extends TimerTask {
        private BufferedMutator mutator = null;

        public FlushTask(BufferedMutator mutator) {
            this.mutator = mutator;
        }

        public void run() {
            try {
                if(mutator != null) {
                    synchronized (mutator) {
                        mutator.flush();
                    }
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
