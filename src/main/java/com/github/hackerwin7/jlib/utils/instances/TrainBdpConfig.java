package com.github.hackerwin7.jlib.utils.instances;

import com.github.hackerwin7.jlib.utils.drivers.url.URLClient;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * get the ip config info
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
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

    private static String JDataGet(String jsonStr, String key) {
        JSONObject jroot = JSONObject.fromObject(jsonStr);
        JSONObject jdata = jroot.getJSONObject("data");
        if(jdata.containsKey(key)) {
            return jdata.getString(key);
        } else {
            return "NOT FOUND KEY = " + key;
        }
    }
}
