package com.github.hackerwin7.jlib.utils.test.executors;

import com.github.hackerwin7.jlib.utils.test.drivers.zk.ZkClient;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/25
 * Time: 3:40 PM
 * Desc:
 */
public class ZkUtilsExecutors {

    /**
     * command for zk client
     * @param args zkConn,cmd,path,data
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String para = args[0];
        String[] paraArr = StringUtils.split(para, ",");
        String zkConn = paraArr[0];
        String cmd = paraArr[1];
        String path = null;
        String data = null;
        ZkClient zk = new ZkClient(zkConn);
        switch (cmd.toLowerCase()) {
            case "set":
                path = paraArr[2];
                data = paraArr[3];
                if(!zk.exists(path))
                    zk.create(path, data);
                else
                    zk.set(path, data);
                break;
            case "get":
                path = paraArr[2];
                System.out.println(zk.get(path));
                break;
            case "rmr":
                path = paraArr[2];
                zk.delete(path);
                break;
            default:
                System.out.println("unsupported command \""+ cmd +"\"");
        }
    }
}
