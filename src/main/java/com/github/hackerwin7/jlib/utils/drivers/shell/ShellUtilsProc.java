package com.github.hackerwin7.jlib.utils.drivers.shell;

import com.github.hackerwin7.jlib.utils.commons.convert.Convert;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/12
 * Time: 10:26 AM
 * Desc: inspired by heron/spi/ShellUtils for process start for sync and async
 *          version 1.0: only these attributes : command, out string, err string
 * Tips:
 */
public class ShellUtilsProc {

    /* logger */
    private static final Logger LOG = Logger.getLogger(ShellUtilsProc.class);

    /**
     * overloaded function with runProcSync(String[] .....)
     * @param cmd
     * @return exit value
     */
    public static int runProcSync(List<String> cmd) {
        return runProcSync(cmd.toArray(new String[cmd.size()]), new StringBuilder(), new StringBuilder());
    }

    /**
     * overloaded function
     * @param cmd
     * @return exit value
     */
    public static int runProcSync(String[] cmd) {
        return runProcSync(cmd, new StringBuilder(), new StringBuilder());
    }

    /**
     * start a ProcessBuilder to start the command and get the stdout and stderr
     * @param cmd
     * @param stdout
     * @param stderr
     * @return exit value, 0 is success
     */
    public static int runProcSync(String[] cmd, StringBuilder stdout, StringBuilder stderr) {

        /* previous check */
        if(cmd.length == 0)
            return -1;
        if(stdout == null)
            stdout = new StringBuilder();
        if(stderr == null)
            stderr = new StringBuilder();

        /* process start */
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return -1;
        }
        InputStream outStream = p.getInputStream();
        InputStream errStream = p.getErrorStream();

        /* stream to string, IO operation, we can parallel */
        Thread outth = Convert.stream2StringBuilderAsync(outStream, stdout);
        Thread errth = Convert.stream2StringBuilderAsync(errStream, stderr);

        /* wait process to die */
        int extVal = 0;
        try {
            extVal = p.waitFor();
            outth.join();
            errth.join();
        } catch (InterruptedException e) {
            outth.interrupt();
            errth.interrupt();
            p.destroy();
            Thread.currentThread().interrupt();
            LOG.error(e.getMessage(), e);
            return -1;
        }

        /* process return */
        LOG.info("STDOUT : " + stdout.toString());
        LOG.info("STDERR : " + stderr.toString());
        return extVal;
    }

    /**
     * overloaded function
     * @param cmd
     * @return process
     */
    public static Process runProcAsync(List<String> cmd) {
        return runProcAsync(cmd.toArray(new String[cmd.size()]));
    }

    /**
     * run async process with ProcessBuilder
     * @param cmd
     * @return async running process
     */
    public static Process runProcAsync(String[] cmd) {

        /* pre check */
        if(cmd.length == 0)
            return null;

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return p;
    }
}