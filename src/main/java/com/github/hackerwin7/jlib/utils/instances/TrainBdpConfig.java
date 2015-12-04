package com.github.hackerwin7.jlib.utils.instances;

import com.github.hackerwin7.jlib.utils.drivers.url.URLClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * get source host in train.bdp.com......
 * Created by fff on 10/28/15.
 */
public class TrainBdpConfig {

    /*logger*/
    private static final Logger logger = Logger.getLogger(TrainBdpConfig.class);

    /*constants*/
    public static final String FILE_NAME = "job.list";
    public static final String BDP_URL = "http://train.bdp.jd.com/api/ztc/job/getJobConfig.ajax?jobId=";

    /*data*/
    public static Set<String> topicSum = new HashSet<String>();

    /**
     * get the ip config info
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Throwable {
        TrainBdpConfig tbc = new TrainBdpConfig();
        tbc.startGetTopic();
    }

    private void startGetTopic() throws Throwable {
        InputStream is = TrainBdpConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        List<String> jobs = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if(!StringUtils.isBlank(line)) {
                jobs.add(line);
            }
        }
        br.close();
        is.close();

        for(String job : jobs) {
            String pjobId = toParserJobId(job);
            String jdata = JDataGetTopic(job, pjobId);
            logger.info(job + " ---------> " + jdata);
            Thread.sleep(500);
        }

        logger.info("topic sum -> ");
        for(String topic : topicSum) {
            logger.info(topic);
        }
    }

    private static String toParserJobId(String jobId) throws Throwable {
        if(jobId.startsWith("2002")) { //old tracker name
            return "21" + jobId.substring(2);
        } else {// new tracker name
            long num = Long.valueOf(jobId);
            return String.valueOf(num + 1);
        }
    }

    private String JDataGetTopic(String tid, String pid) throws Throwable {
        JSONObject jtdata = JSONObject.fromObject(URLClient.getFromUrl(BDP_URL + tid)).getJSONObject("data");
        String ip = null;
        if(jtdata.containsKey("source_host")) {
            ip = jtdata.getString("source_host");
        } else {
            ip = "NOT FOUND key = source_host";
        }
        String ttopic = null;
        if(jtdata.containsKey("tracker_log_topic")) {
            ttopic = jtdata.getString("tracker_log_topic");
            //sum topic
            topicSum.add(ttopic);
        } else {
            ttopic = "NOT FOUND key = tracker_log_topic";
        }
        JSONObject jpdata = JSONObject.fromObject(URLClient.getFromUrl(BDP_URL + pid)).getJSONObject("data");
        String topicStr = null;
        if(jpdata.containsKey("db_tab_meta")) {
            JSONArray jparr = jpdata.getJSONArray("db_tab_meta");
            Set<String> topics = new HashSet<String>();
            for(int i = 0; i <= jparr.size() - 1; i++) {
                String ptopic = jparr.getJSONObject(i).getString("topic");
                topics.add(ptopic);
                topicSum.add(ptopic);
            }
            topicStr = StringUtils.join(topics, ",");
        } else {
            topicStr = "NOT FOUND key = db_tab_meta";
        }
        return ip + "=====>" + ttopic + " =====> " + topicStr;
    }

    private void startGetIp() throws Throwable {
        InputStream is = TrainBdpConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        List<String> jobs = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if(!StringUtils.isBlank(line)) {
                jobs.add(line);
            }
        }
        br.close();
        is.close();

        for(String job : jobs) {
            String jstr = URLClient.getFromUrl(BDP_URL + job);
            String ip = JDataGet(jstr, "source_host");
            logger.info(job + " ---------> " + ip);
            Thread.sleep(500);
        }
    }

    private String JDataGet(String jsonStr, String key) {
        JSONObject jroot = JSONObject.fromObject(jsonStr);
        JSONObject jdata = jroot.getJSONObject("data");
        if(jdata.containsKey(key)) {
            return jdata.getString(key);
        } else {
            return "NOT FOUND KEY = " + key;
        }
    }
}
