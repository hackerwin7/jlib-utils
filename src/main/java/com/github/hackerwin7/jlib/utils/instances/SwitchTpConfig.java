package com.github.hackerwin7.jlib.utils.instances;

import com.github.hackerwin7.jlib.utils.drivers.url.URLClient;
import com.github.hackerwin7.jlib.utils.utils.convert.Convert;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.htrace.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/11
 * Time: 10:46 AM
 * Desc:
 */
public class SwitchTpConfig {

    private static DefaultHttpClient httpClient = new DefaultHttpClient();
    private static DefaultHttpClient httpclient1 = new DefaultHttpClient();

    public static final String KEY_FORMAT = ".jrdw.jd.com";

    public static void main(String[] args) throws Exception {
        for(int i = 1000174; i <= 1000174; i+=2) {
            String constrt = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + i);
            String constr = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + (i + 1));
            JSONObject jconf = JSONObject.fromObject(constr);
            JSONObject jconft = JSONObject.fromObject(constrt);
            writeConf("tp-" + i + KEY_FORMAT, switchConf(jconf, jconft).toString());
            //getConf("tp-" + i + KEY_FORMAT);
        }
    }
    private static JSONObject switchConf(JSONObject old, JSONObject oldt) throws Exception {
        old.getJSONObject("data").put("source_host", oldt.getJSONObject("data").getString("source_host"));
        old.getJSONObject("data").put("source_charset", oldt.getJSONObject("data").getString("source_charset"));
        old.getJSONObject("data").put("source_password", oldt.getJSONObject("data").getString("source_password"));
        old.getJSONObject("data").put("source_port", oldt.getJSONObject("data").getString("source_port"));
        old.getJSONObject("data").put("source_slaveId", oldt.getJSONObject("data").getString("source_slaveId") + "123");
        old.getJSONObject("data").put("source_user", oldt.getJSONObject("data").getString("source_user"));
        JSONArray jarr = old.getJSONObject("data").getJSONArray("db_tab_meta");
        for(int i = 0; i <= jarr.size() - 1; i++) {
            JSONObject jdt = jarr.getJSONObject(i);
            String table = jdt.getString("tablename");
            if(table.startsWith("orders_"))
                jdt.put("htable", "orders");
            else if(table.startsWith("orderdetail_"))
                jdt.put("htable", "orderdetail");
            else
                throw new Exception("error table name = " + table);
        }
        return old;
    }
    private static void writeConf(String jobId, String value) throws Exception {
        String enc = "UTF-8";
        String appid = "bdp.jd.com";
        appid = URLEncoder.encode(appid, enc);
        String token = "RQLMPXULF3EG23CPZL3U257B7Y";
        token = URLEncoder.encode(token, enc);
        String addr = "http://atom.bdp.jd.local/api/wordbook/save";
        String time = String.valueOf(System.currentTimeMillis());
        //data
        JSONObject jdata = new JSONObject();
        jdata.put("key", jobId);
        jdata.put("value", value);
        jdata.put("status", "1");
        jdata.put("manager", new String[] {"fff"});
        jdata.put("relatedUser", new String[] {"fff"});
        jdata.put("erp", "fff");
        String data = jdata.toString();
        data = URLEncoder.encode(data, enc);
        //System.out.println(data);
        //post url
        String url = addr + "?appId=" + appid + "&token=" + token + "&time=" + time + "&data=" + data;
        System.out.println(url.length());
//        HttpPost post = new HttpPost(url);
//        HttpResponse response = httpClient.execute(post);
//        JSONObject jres = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), enc));
//        System.out.println(jres.toString());
    }
    private static void getConf(String jobId) throws Exception {
        String enc = "UTF-8";
        String appid = "bdp.jd.com";
        appid = URLEncoder.encode(appid, enc);
        String token = "RQLMPXULF3EG23CPZL3U257B7Y";
        token = URLEncoder.encode(token, enc);
        String addr = "http://atom.bdp.jd.local/api/wordbook/get";
        String time = String.valueOf(System.currentTimeMillis());
        //data
        JSONObject jdata = new JSONObject();
        jdata.put("id", 12L);
        jdata.put("erp", "fff");
        String data = jdata.toString();
        data = URLEncoder.encode(data, enc);
        String url = addr + "?appId=" + appid + "&token=" + token + "&time=" + time + "&data=" + data;
        HttpPost post = new HttpPost(url);
        HttpResponse response = httpclient1.execute(post);
        JSONObject jres = JSONObject.fromObject(EntityUtils.toString(response.getEntity(), enc));
        System.out.println(jres.toString());
    }
}
