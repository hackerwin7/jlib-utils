package com.github.hackerwin7.jlib.utils.test.drivers.zk;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;

import java.util.List;

/**
 * zookeeper client api
 * Created by fff on 10/28/15.
 */
public class ZkClient {

    /*logger*/
    private static final Logger logger = Logger.getLogger(ZkClient.class);

    /*driver*/
    private ZooKeeper zk = null;

    /**
     * zookeeper client, connection string
     * @param zkConn
     * @throws Exception
     */
    public ZkClient(String zkConn) throws Exception {
        zk = new ZooKeeper(zkConn, 100000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                logger.info("watcher : " + watchedEvent.getType());
            }
        });
    }

    /**
     * close the zk driver
     * @throws Exception
     */
    public void close() throws Exception {
        zk.close();
    }

    /**
     * the string path is exists
     * @param path
     * @return boolean path node
     * @throws Exception
     */
    public boolean exists(String path) throws Exception {
        if(zk.exists(path, false) == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * recursively check
     * @param path
     * @return bool
     * @throws Exception
     */
    public boolean existsR(String path) throws Exception {
        String[] items = StringUtils.split(path, "/");
        StringBuilder rec = new StringBuilder();
        for(String item : items) {
            rec.append("/").append(item);
            if(!exists(rec.toString()))
                return false;
        }
        return true;
    }

    /**
     * create the path and data
     * @param path
     * @param data
     * @throws Exception
     */
    public void create(String path, String data) throws Exception {
        if(data == null)
            data = "";
        zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    /**
     * reload func
     * @param path
     * @param data
     * @throws Exception
     */
    public void create(String path, String data, CreateMode mode) throws Exception {
        if(data == null)
            data = "";
        zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
    }

    /**
     * recursively create
     * @param path
     * @param data
     * @throws Exception
     */
    public void createR(String path, String data) throws Exception {
        String[] items = StringUtils.split(path, "/");
        StringBuilder rec = new StringBuilder();
        for(String item : items) {
            rec.append("/").append(item);
            if(!exists(rec.toString()))
                create(rec.toString(), null);
        }
        set(path, data);
    }

    /**
     * set the zookeeper node
     * @param path
     * @param data
     * @throws Exception
     */
    public void set(String path, String data) throws Exception {
        zk.setData(path, data.getBytes(), -1);
    }

    /**
     * set recursively the process same as createR
     * @param path
     * @param data
     * @throws Exception
     */
    public void setR(String path, String data) throws Exception {
        createR(path, data);
    }

    /**
     * getOrigin the zookeeper node
     * @param path
     * @throws Exception
     */
    public String get(String path) throws Exception {
        return new String(zk.getData(path, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                logger.info("get data watcher : " + watchedEvent.getType());
            }
        }, null));
    }


    /**
     * getOrigin children node
     * @param path
     * @return list of children node name
     * @throws Exception
     */
    public List<String> getChildren(String path) throws Exception {
        return zk.getChildren(path, false);
    }

    /**
     * delete the zk node
     * @param path
     * @throws Exception
     */
    public void delete(String path) throws Exception {
        zk.delete(path, -1);
    }
}
