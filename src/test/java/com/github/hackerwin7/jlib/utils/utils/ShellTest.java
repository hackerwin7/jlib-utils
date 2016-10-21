package com.github.hackerwin7.jlib.utils.utils;

import com.github.hackerwin7.jlib.utils.drivers.shell.ShellCommand;
import com.github.hackerwin7.jlib.utils.drivers.shell.ShellUtilsProc;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
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

    private final File workingDir = new File("/home/fff/Tmp");

    public static void main(String[] args) throws Exception {

        ShellTest st = new ShellTest();

//        String cmd = "top -b -n 1";
//        List<String> rets =  ShellClient.execute(cmd);
//        System.out.println(rets);

//        pbexec();

//        st.tar();


//        st.env();
        st.ln();
        st.unln();
        st.islnT();
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

    public void ln() throws IOException {
        ShellCommand.exec(workingDir, "ln", "-s", "zookeeper-3.4.8", "zookeeper");
    }

    public void unln() throws IOException {
        ShellCommand.exec(workingDir, "unlink", "zookeeper");
    }

    public void env() throws IOException {
        System.out.println(System.getenv());
    }

    public boolean isLink(String name) throws IOException {
//        OutputStream out = new ByteArrayOutputStream();
//        ExecuteStreamHandler streamHandler = new PumpStreamHandler(out);
//        ShellCommand.exec(streamHandler, workingDir, "ls", "-la");
//        String outs = out.toString();
        File[] files = workingDir.listFiles();
        for(File file : files) {
            if(StringUtils.equals(name, file.getName()) && !StringUtils.equals(file.getAbsolutePath(), file.getCanonicalPath())) // sequence is important
                return true;
        }
        return false;
    }

    public void islnT() {
        try {
            System.out.println(isLink("zookeeper"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
