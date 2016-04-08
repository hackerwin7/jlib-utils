package com.github.hackerwin7.jlib.utils.drivers.shell;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/02/01
 * Time: 5:04 PM
 * Desc: a java shell client to execute shell and return a response info
 */
public class ShellClient {

    /* logger */
    private Logger logger = Logger.getLogger(ShellClient.class);

    /**
     * execute the command
     * @param cmd
     * @return return string list
     * @throws Exception
     */
    public static List<String> execute(String cmd) throws Exception {
        List<String> rets = new LinkedList<>();
        String [] cmdArr = {
                "/bin/sh",
                "-c",
                cmd
        };
        Process proc = Runtime.getRuntime().exec(cmdArr);//single cmd string is insvalid in some cases
        proc.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            rets.add(line);
        }
        return rets;
    }

    /**
     * test for the method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String cmd = "ssh magpie@172.22.178.176 \"mysql -h172.19.148.68 -ucanal -P3358 -pFdHTbSjheGVNQwEDNSXXOO_D2efdsdF -e \'show master status;\'\"";
        //String cmd = "ssh magpie@172.22.178.85 \"ssh 172.22.178.176 \"mysql -h10.191.66.89 -umagpie -pFdHTbSjheGVNQwEDNSXXOO_D2efdsdF -P3358 -e \"show variables like '%log_%'; show master status;\"\"\"";
        //String cmd = "ssh magpie@172.22.178.85 \"ssh 172.22.178.176 \"mysql -h10.191.66.89 -umagpie -pFdHTbSjheGVNQwEDNSXXOO_D2efdsdF -P3358 -e \'show master status;\'\"\"";
        //String cmd = "ssh magpie@172.22.178.85 \"magpie-client info -list assignments | grep mysql-tracker | awk -F \" \" \"print {$5}\"\"";
        List<String> rets = execute(cmd);
        for(String ret : rets) {
            System.out.println(ret);
            String[] strArr = StringUtils.split(ret, "\t");
            for(String str : strArr) {
                System.out.println(str);
            }
        }
    }
}
