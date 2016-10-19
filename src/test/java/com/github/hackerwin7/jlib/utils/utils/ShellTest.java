package com.github.hackerwin7.jlib.utils.utils;

import com.github.hackerwin7.jlib.utils.drivers.shell.ShellUtilsProc;
import com.sun.xml.txw2.output.SaxSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
//        pbexec();
        ShellTest st = new ShellTest();
        st.tar();
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

    public void tar() {
        List<String> cmds = new ArrayList<>();
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        String cmd = "tar";
        String cmd1 = "xvf";
        String target = "/home/fff/Tmp" + "/" + "zookeeper-3.4.8.tar.gz";
        String attributes = "-C";
        String attributes1 = "/home/fff/Tmp";
        cmds.add(cmd);
        cmds.add(cmd1);
        cmds.add(target);
        cmds.add(attributes);
        cmds.add(attributes1);
        ShellUtilsProc.runProcSync(cmds, stdout, stderr);
        System.out.println("out:\n" + stdout.toString());
        System.out.println("err:\n" + stderr.toString());
    }
}
