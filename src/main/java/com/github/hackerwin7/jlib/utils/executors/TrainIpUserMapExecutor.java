package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.file.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/31
 * Time: 2:28 PM
 * Desc:
 */
public class TrainIpUserMapExecutor {
    public static void main(String[] args) throws Exception {
        List<String> ips = FileUtils.file2List("row.list");
        List<String> usrs = FileUtils.file2List("row1.list");
        Map<String, String> iu = new HashMap<>();
        for(int i = 0; i <= ips.size() - 1; i++) {
            String ip = ips.get(i);
            String user = usrs.get(i);
            if(iu.containsKey(ip)) {
                if(!StringUtils.equalsIgnoreCase(iu.get(ip), user)) {
                    iu.put(ip, iu.get(ip) + "/" + user);
                }
            } else {
                iu.put(ip, user);
            }
        }
        for(Map.Entry<String, String> entry : iu.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
