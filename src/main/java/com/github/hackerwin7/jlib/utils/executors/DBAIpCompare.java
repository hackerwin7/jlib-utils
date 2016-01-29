package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.file.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by fff on 12/21/15.
 */
public class DBAIpCompare {
    public static void main(String[] args) throws Exception {
        DBAIpCompare dba = new DBAIpCompare();
        dba.mainProc();
        //dba.distinct();
    }

    public void mainProc() throws Exception {
        Set<String> ips = loadIpList("ip.list");
        Set<String> ipd = loadIpList("ip_dba.list");
        Set<String> res = new HashSet<>();
        res.addAll(ips);
        res.retainAll(ipd);
        for(String ip : res) {
            System.out.println(ip);
        }
    }

    private Set<String> loadIpList(String fileName) throws Exception {
        InputStream is = TrainBdpConfig.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        Set<String> jobs = new HashSet<>();
        while ((line = br.readLine()) != null) {
            if(!StringUtils.isBlank(line)) {
                jobs.add(line);
            }
        }
        br.close();
        is.close();
        return jobs;
    }

    public void distinct() throws Exception {
        List<String> ips = FileUtils.file2List("ip.list");
        Set<String> desIps = new HashSet<>();
        for(String ip : ips) {
            desIps.add(ip);
        }
        for(String ip : desIps) {
            System.out.println(ip);
        }
    }
}
