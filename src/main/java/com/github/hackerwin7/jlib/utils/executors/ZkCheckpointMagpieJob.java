package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.drivers.zk.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * set zk checkpoint to tracker and parser
 * Created by fff on 10/28/15.
 */
public class ZkCheckpointMagpieJob {

    private static final Logger logger = Logger.getLogger(ZkCheckpointMagpieJob.class);

    public static final String JOB_LIST = "job.list";
    public static final String SHELL_CONF = "config.conf";

    public static final String SET_CMD = "set";

    public static final String CHECKPOINT_PATH = "/checkpoint";

    public static void main(String[] args) throws Exception {
        String zkConn = args[0];
        String cmd = args[1];
        String val = args[2];

        ZkClient zk = new ZkClient(zkConn);

        InputStream is = CommonUtils.file2in(JOB_LIST, SHELL_CONF);
        BufferedReader br =  new BufferedReader(new InputStreamReader(is));

        String line = null;
        List<String> jobs = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            if(StringUtils.isBlank(line)) {
                continue;
            }
            jobs.add(line);
        }

        br.close();
        is.close();

        for(String job : jobs) {
            execute(zk, job, cmd, val);
        }

        //verify the values
        for(String job : jobs) {
            verify(zk, job);
        }
    }

    public static void execute(ZkClient zk, String jobId, String cmd, String val) throws Exception {
        switch (cmd) {
            case SET_CMD:
                set(zk, jobId, val);
                break;
            default:
                break;
        }
    }

    public static void set(ZkClient zk, String jobId, String val) throws Exception {
        if(isTracker(jobId)) { //tracker
            zk.set(CHECKPOINT_PATH + "/" + jobId + "/" + "T", val);
        } else { // parser
            zk.set(CHECKPOINT_PATH + "/" + jobId + "/" + "P", val);
            List<String> children = zk.getChildren(CHECKPOINT_PATH + "/" + jobId);
            for(String ch : children) {
                if(!StringUtils.equals(ch, "T") && !StringUtils.equals(ch, "P")) {
                    zk.set(CHECKPOINT_PATH + "/" + jobId + "/" + ch + "/0", val);
                }
            }
        }
    }

    public static boolean isTracker(String jobId) throws Exception {
        int rear = Integer.valueOf(jobId.substring(jobId.length() - 1, jobId.length()));
        if(rear % 2 == 0) { // tracker
            return true;
        } else { // parser
            return false;
        }
    }

    public static void verify(ZkClient zk, String jobId) throws Exception {
        if(isTracker(jobId)) {
            logger.info("-----------> verify node tracker " + jobId + " = " + zk.get(CHECKPOINT_PATH + "/" + jobId + "/" + "T"));
        } else {
            List<String> children = zk.getChildren(CHECKPOINT_PATH + "/" + jobId);
            List<String> vals = new ArrayList<String>();
            String mulVal = null;
            for(String ch : children) {
                if(!StringUtils.equals(ch, "T") && !StringUtils.equals(ch, "P")) {
                    vals.add(zk.get(CHECKPOINT_PATH + "/" + jobId + "/" + ch + "/0"));
                }
                mulVal = StringUtils.join(vals, ",");
            }
            logger.info("-----------> verify node parser " + jobId + " = " + zk.get(CHECKPOINT_PATH + "/" + jobId + "/" + "P") + ", " + mulVal);
        }
    }
}
