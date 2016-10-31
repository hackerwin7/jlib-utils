package com.github.hackerwin7.jlib.utils.sample;

import com.github.hackerwin7.jlib.utils.drivers.shell.ShellUtilsProc;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/31
 * Time: 11:13 AM
 * Desc:
 * Tips:
 */
public class ProcessBuilderTest {

    private static final Logger LOG = Logger.getLogger(ProcessBuilderTest.class);

    public static void main(String[] args) {
        ProcessBuilderTest pbt = new ProcessBuilderTest();
        pbt.start1();
    }

    public void start() {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        List<String> cmds = new ArrayList<>();
        cmds.add("./bin/start.sh");
        cmds.add("echo $PPID");
        ShellUtilsProc.runProcSync(cmds, stdout, stderr, new File("/home/fff/Servers/jlib-0.1.1"));
        LOG.info("started the process ......");
        LOG.info("stdout = " + stdout.toString());
        LOG.info("stderr = " + stderr.toString());
    }

    public void start1() {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        List<String> cmds = new ArrayList<>();
        cmds.add("java");
        cmds.add("-cp");
        cmds.add("jlib-0.1.1.jar");
        cmds.add("com.github.hackerwin7.jlib.utils.executors.ProcessBuilderId");
        cmds.add("&");
        //cmds.add("1>>/dev/null 2>&1 &");
        //cmds.add("java -cp jlib-0.1.1.jar com.github.hackerwin7.jlib.utils.executors.ProcessBuilderId 1>>/dev/null 2>&1 &");
        ShellUtilsProc.runProcSync(cmds, stdout, stderr, new File("/home/fff/Projects/jlib-utils/target"));
        LOG.info("started the process ......");
        LOG.info("stdout = " + stdout.toString());
        LOG.info("stderr = " + stderr.toString());
    }
}
