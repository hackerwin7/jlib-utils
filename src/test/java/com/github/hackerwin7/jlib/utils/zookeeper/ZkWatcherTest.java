package com.github.hackerwin7.jlib.utils.zookeeper;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/08/11
 * Time: 2:38 PM
 * Desc:
 * Tips:
 */
public class ZkWatcherTest {

    public static final String PATH_TEST = "/test/t1";

    public static void main(String[] args) throws Exception {
        ZkWatcherTest zwt = new ZkWatcherTest();
        zwt.start();
    }

    public void start() throws Exception {
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 300000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                String path = event.getPath();
                Event.EventType type = event.getType();
                if(StringUtils.equals(path, PATH_TEST) && type == Event.EventType.NodeDeleted) {
                    //do sth
                    System.out.println("deleting watching");
                }
            }
        });
        ZooKeeper zk1 = new ZooKeeper("127.0.0.1:2181", 300000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //nothing
            }
        });
        if(zk1.exists("/test", false) == null)
            zk1.create("/test", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk1.create(PATH_TEST, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        //zk.exists(PATH_TEST, true);// set watch on the node
        Thread.sleep(1000);
        zk1.close();
        System.out.println("deleted");
        Thread.sleep(5000);
    }
}
