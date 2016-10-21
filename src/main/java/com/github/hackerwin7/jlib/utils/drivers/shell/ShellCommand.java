package com.github.hackerwin7.jlib.utils.drivers.shell;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/20
 * Time: 2:40 PM
 * Desc: shell command execute utils
 * Tips:
 */
public class ShellCommand {

    /**
     * transfer CommandLine
     * @param cmd
     * @return command line
     */
    private static CommandLine commandLine(String[] cmd) {
        CommandLine cl = new CommandLine(cmd[0]);
        for(int i = 1; i <= cmd.length - 1; i++) {
            cl.addArgument(cmd[i]);
        }
        return cl;
    }

    /**
     * exec command and no return string info but signal info
     * @param command
     * @return false -> failed, true -> success
     */
    public static boolean exec(String... command) throws IOException {
        return exec(null, null, null, command);
    }

    public static boolean exec(Map<String, String> env, String... command) throws IOException {
        return exec(null, null, env, command);
    }

    public static boolean exec(File workingDir, String... command) throws IOException {
        return exec(null, workingDir, null, command);
    }

    public static boolean exec(ExecuteStreamHandler streamHandler, File workingDir, String... command) throws IOException {
        return exec(streamHandler, workingDir, null, command);
    }

    public static boolean exec(File workingDir, Map<String, String> env, String... command) throws IOException {
        return exec(null, workingDir, env, command);
    }

    public static boolean exec(ExecuteStreamHandler streamHandler, File workingDir, Map<String, String> env, String... command) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        if(streamHandler != null)
            executor.setStreamHandler(streamHandler);
        if(workingDir != null)
            executor.setWorkingDirectory(workingDir);
        return exec(executor, commandLine(command), env);
    }

    /**
     * exec command and no return string info but signal info
     * @param executor
     * @param cl
     * @param env
     * @return false -> failed, true -> success
     * @throws IOException
     */
    private static boolean exec(DefaultExecutor executor, CommandLine cl, Map<String, String> env) throws IOException {
        return !executor.isFailure(executor.execute(cl, env));
    }
}
