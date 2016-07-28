package com.github.hackerwin7.jlib.utils.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/04/21
 * Time: 4:36 PM
 * Desc:
 * Tips:
 */
public class ShellTest {
    public static void main(String[] args) throws Exception {
//        String cmd = "top -b -n 1";
//        List<String> rets =  ShellClient.execute(cmd);
//        System.out.println(rets);
        pbexec();
    }

    private static void pbexec() throws Exception {
        String[] cmds = {
                "top",
                "-b",
                "-n",
                "1"
        };
        ProcessBuilder builder = new ProcessBuilder(cmds);
        Process proc = builder.start();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = stdin.readLine()) != null) {
            sb.append(line).append("\r\n");
        }
        System.out.println(sb.toString());
    }
}
