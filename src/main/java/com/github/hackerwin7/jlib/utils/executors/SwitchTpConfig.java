package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.url.URLClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

    public static final int TIMEOUT = 300000;

    static {
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);


        httpclient1.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
        httpclient1.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT);
    }

    public static final String KEY_FORMAT = ".jrdw.jd.com";

    private static int ordersNum = 0;

    public static void main(String[] args) throws Exception {
        String instr = "1000174,1000176,1000178,1000180,1000182,1000184,1000186,1000188,1000190,1000192,1000194,1000196,1000198,1000200,1000202,1000204,1000206,1000208,1000210,1000212";
        String[] strArr = StringUtils.split(instr, ",");
        for(String sid : strArr) {
            String tid = sid;
            String pid = String.valueOf(Integer.valueOf(tid) + 1);
            String constrt = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + tid);
            String constr = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + pid);
            JSONObject jconf = JSONObject.fromObject(constr);
            JSONObject jconft = JSONObject.fromObject(constrt);
            writeConf("tp-" + tid + KEY_FORMAT, switchConf(jconf, jconft).getJSONObject("data").toString());
            getConf("tp-" + tid + KEY_FORMAT);
            ordersNum++;
        }
    }
    private static JSONObject switchConf(JSONObject old, JSONObject oldt) throws Exception {
        old.getJSONObject("data").put("source_host", oldt.getJSONObject("data").getString("source_host"));
        old.getJSONObject("data").put("source_charset", oldt.getJSONObject("data").getString("source_charset"));
        old.getJSONObject("data").put("source_password", oldt.getJSONObject("data").getString("source_password"));
        old.getJSONObject("data").put("source_port", oldt.getJSONObject("data").getString("source_port"));
        old.getJSONObject("data").put("source_slaveId", Long.valueOf(oldt.getJSONObject("data").getString("source_slaveId") + "127"));
        old.getJSONObject("data").put("source_user", oldt.getJSONObject("data").getString("source_user"));
        old.getJSONObject("data").put("hbase_tablename", "rbdm:" + "jdorders_" + ordersNum);
        old.getJSONObject("data").put("target.quorum", "172.19.186.89,172.19.186.90,172.19.186.91,172.19.186.93,172.19.186.93");
        old.getJSONObject("data").put("target.clientport", "2181");
        old.getJSONObject("data").put("target.hbase.zkroot", "/hbase_paris");
        old.getJSONObject("data").put("family", "d");
        old.getJSONObject("data").put("rowkey_type", "long");

        JSONArray jarr = old.getJSONObject("data").getJSONArray("db_tab_meta");
        for(int i = 0; i <= jarr.size() - 1; i++) {
            JSONObject jdt = jarr.getJSONObject(i);
            String table = jdt.getString("tablename");
            jdt.put("htable", table.substring(0, table.indexOf("_")));
        }
        return old;
    }
    private static void writeConf(String jobId, String value) throws Exception {
        //value make
        String enc = "UTF-8";
        String appid = "bdp.jd.com";
        appid = URLEncoder.encode(appid, enc);
        String token = "RQLMPXULF3EG23CPZL3U257B7Y";
        token = URLEncoder.encode(token, enc);
        String addr = "http://atom.bdp.jd.local/api/site/save";
        String time = String.valueOf(System.currentTimeMillis());
        //data
        JSONObject jdata = new JSONObject();
        jdata.put("key", jobId);
        jdata.put("value", StringEscapeUtils.escapeJson(value));
        jdata.put("model", "rpc");


        jdata.put("synchronous", true);
        jdata.put("erp", "fff");
        String data = jdata.toString();
        //post url
        String url = addr;
        HttpPost post = new HttpPost(url);
        List<NameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("appId", appid));
        valuePairs.add(new BasicNameValuePair("token", token));
        valuePairs.add(new BasicNameValuePair("time", time));
        valuePairs.add(new BasicNameValuePair("data", data));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, enc);
        post.setEntity(formEntity);
        HttpResponse response = httpClient.execute(post);
        String ret = EntityUtils.toString(response.getEntity(), enc);
        System.out.println(ret);
    }
    private static void getConf(String jobId) throws Exception {
        String enc = "UTF-8";
        String appid = "bdp.jd.com";
        appid = URLEncoder.encode(appid, enc);
        String token = "RQLMPXULF3EG23CPZL3U257B7Y";
        token = URLEncoder.encode(token, enc);
        String addr = "http://atom.bdp.jd.local/api/site/get";
        String time = String.valueOf(System.currentTimeMillis());
        //data
        JSONObject jdata = new JSONObject();
        jdata.put("model", "rpc");
        jdata.put("key", jobId);
        jdata.put("erp", "fff");
        String data = jdata.toString();
        //post url
        String url = addr;
        HttpPost post = new HttpPost(url);
        List<NameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("appId", appid));
        valuePairs.add(new BasicNameValuePair("token", token));
        valuePairs.add(new BasicNameValuePair("time", time));
        valuePairs.add(new BasicNameValuePair("data", data));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(valuePairs, enc);
        post.setEntity(formEntity);
        HttpResponse response = httpClient.execute(post);
        String ret = EntityUtils.toString(response.getEntity(), enc);
        System.out.println(ret);
    }

    private static void getConf1(String jobId) throws Exception {
        String enc = "UTF-8";
        String appid = "bdp.jd.com";
        appid = URLEncoder.encode(appid, enc);
        String token = "RQLMPXULF3EG23CPZL3U257B7Y";
        token = URLEncoder.encode(token, enc);
        String addr = "http://test.atom.bdp.jd.com/api/site/get";
        String time = String.valueOf(System.currentTimeMillis());
        //data
        JSONObject jdata = new JSONObject();
        jdata.put("model", "rpc");
        jdata.put("key", jobId);
        jdata.put("erp", "fff");
        String data = jdata.toString();
        data = URLEncoder.encode(data, enc);
        //post url
        String url = addr + "?appId=" + appid + "&token=" + token + "&time=" + time + "&data=" + data;
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        String ret = EntityUtils.toString(response.getEntity(), enc);
        System.out.println(ret);
    }

    private static void getConf2(String jobId) throws Exception {
        String enc = "UTF-8";
        String appid = "bdp.jd.com";
        appid = URLEncoder.encode(appid, enc);
        String token = "RQLMPXULF3EG23CPZL3U257B7Y";
        token = URLEncoder.encode(token, enc);
        String addr = "http://test.atom.bdp.jd.com/api/site/get";
        String time = String.valueOf(System.currentTimeMillis());
        //data
        String data = "{\"key\":\"" + jobId + "\",\"erp\":\"fanwenqi\"," + "\"model\":\"rpc\"}";
        data = URLEncoder.encode(data, enc);
        //post url
        String url = addr + "?appId=" + appid + "&token=" + token + "&time=" + time + "&data=" + data;
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        String ret = EntityUtils.toString(response.getEntity(), enc);
        System.out.println(ret);

        //show config
        JSONObject jret = JSONObject.fromObject(ret);
        String origin = jret.getString("obj");
        String value = StringEscapeUtils.unescapeJson(origin);
        System.out.println(JSONObject.fromObject(value));
    }

    public String switchJob(String jobId) throws Exception {
        //get tracker jodid and parser jobid
        String tid = jobId;
        String pid = tid.replaceFirst("2002", "2102");
        String tconfStr = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + tid);
        String pconfStr = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + pid);
        JSONObject tjson = JSONObject.fromObject(tconfStr);
        JSONObject pjson = JSONObject.fromObject(pconfStr);
        JSONObject tpjson = switchConf(pjson, tjson);
        return tpjson.toString();
    }
}
