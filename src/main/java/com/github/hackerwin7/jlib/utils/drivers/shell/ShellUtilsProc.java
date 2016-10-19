package com.github.hackerwin7.jlib.utils.drivers.shell;

import com.github.hackerwin7.jlib.utils.commons.convert.Convert;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/10/12
 * Time: 10:26 AM
 * Desc: inspired by heron/spi/ShellUtils for process start for sync and async
 *          version 1.0: only these attributes : command, out string, err string
 *          version 2.0: add working directory; environment and inherit IO
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
        return runProcSync(cmd, null, null);
    }

    /**
     * overloaded function
     * @param cmd
     * @param stdout
     * @param stderr
     * @return exit value
     */
    public static int runProcSync(List<String> cmd, StringBuilder stdout, StringBuilder stderr) {
        return runProcSync(cmd, stdout, stderr, null, null, false);
    }

    public static int runProcSync(List<String> cmd, StringBuilder stdout, StringBuilder stderr,
                                  File workingDir, Map<String, String> envi, boolean isInheritIO) {
        return runProcSync(cmd.toArray(new String[cmd.size()]), stdout, stderr, workingDir, envi, isInheritIO);
    }

    /**
     * overloaded function
     * @param cmd
     * @return exit value
     */
    public static int runProcSync(String[] cmd) {
        return runProcSync(cmd, null, null);
    }

    public static int runProcSync(String[] cmd, StringBuilder stdout, StringBuilder stderr) {
        return runProcSync(cmd, stdout, stderr, null, null, false);
    }

    /**
     * start a ProcessBuilder to start the command and get the stdout and stderr
     * @param cmd
     * @param stdout
     * @param stderr
     * @param workingDir
     * @param envi
     * @param isInheritIO
     * @return exit value, 0 is success
     */
    public static int runProcSync(String[] cmd, StringBuilder stdout, StringBuilder stderr,
                                  File workingDir, Map<String, String> envi, boolean isInheritIO) {

        /* previous check */
        if(cmd.length == 0)
            return -1;
        if(stdout == null)
            stdout = new StringBuilder();
        if(stderr == null)
            stderr = new StringBuilder();

        /* process start */
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if(workingDir != null) // working directory
            pb.directory(workingDir);
        if(envi != null) { // environment setting
            Map<String, String> env = pb.environment();
            for (Map.Entry<String, String> entry : envi.entrySet()) {
                env.put(entry.getKey(), entry.getValue());
            }
        }
        if(isInheritIO) // inherit IO
            pb.inheritIO();
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
        LOG.debug("STDOUT : " + stdout.toString());
        LOG.debug("STDERR : " + stderr.toString());
        return extVal;
    }

    /**
     * overloaded function
     * @param cmd
     * @return process
     */
    public static Process runProcAsync(List<String> cmd) {
        return runProcAsync(cmd, null, null);
    }

    public static Process runProcAsync(List<String> cmd, File workingDir, Map<String, String> env) {
        return runProcAsync(cmd.toArray(new String[cmd.size()]), workingDir, env);
    }

    public static Process runProcAsync(String[] cmd) {
        return runProcAsync(cmd, null, null);
    }

    /**
     * run async process with ProcessBuilder
     * @param cmd
     * @param workingDir
     * @param envi
     * @return async running process
     */
    public static Process runProcAsync(String[] cmd, File workingDir, Map<String, String> envi) {

        /* pre check */
        if(cmd.length == 0)
            return null;

        ProcessBuilder pb = new ProcessBuilder(cmd);
        if(workingDir != null) // working directory
            pb.directory(workingDir);
        if(envi != null) { // environment setting
            Map<String, String> env = pb.environment();
            for (Map.Entry<String, String> entry : envi.entrySet()) {
                env.put(entry.getKey(), entry.getValue());
            }
        }
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return p;
    }
}