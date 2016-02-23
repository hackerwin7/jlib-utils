package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.zk.ZkClient;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/08
 * Time: 1:44 PM
 * Desc:
 */
public class ZkUtilCommand {

    private String zkconn = "172.19.176.19:2181,172.19.176.20:2181,172.19.176.21:2181,172.19.176.22:2181,172.19.176.23:2181";

    public static void main(String[] args) throws Exception {
        ZkUtilCommand demo = new ZkUtilCommand();
        String connStr = args[0];
        String cmd = args[1];
        String path = args[2];
        String data = args[3];
        demo.run(connStr, cmd, path, data);
    }

    public void run(String connStr, String cmd, String path, String data) throws Exception {
        ZkClient zk = new ZkClient(connStr);
        switch (cmd) {
            case "set":
                zk.set(path, data);
                break;
            case "get":
                System.out.println(zk.get(path));
                break;
            case "rmr":
                zk.delete(path);
                break;
            case "crt":
                zk.create(path, data);
                break;
        }
    }
}
