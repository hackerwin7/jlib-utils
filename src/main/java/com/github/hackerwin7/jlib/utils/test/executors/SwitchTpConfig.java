package com.github.hackerwin7.jlib.utils.test.executors;

import com.github.hackerwin7.jlib.utils.test.drivers.file.FileUtils;
import com.github.hackerwin7.jlib.utils.test.drivers.url.URLClient;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.Charset;
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
    public static final String KEY_FORMAT_TEST = KEY_FORMAT + "_test";

    private static String hbaseJobId = "";

    private static int ordersNum = 0;

    // jdorders,ocs,product_pop,product_book_self_other,customer
    private static String hbaseMidName = "product_pop";

    public static void main(String[] args) throws Exception {
        //jdorders
        //String instr = "1000174,1000176,1000178,1000180,1000182,1000184,1000186,1000188,1000190,1000192,1000194,1000196,1000198,1000200,1000202,1000204,1000206,1000208,1000210,1000212";
        //String[] strArr = StringUtils.split(instr, ",");

        List<String> strArr = FileUtils.file2List("job.list");
        for(String sid : strArr) {
            String tid = sid;
            String pid = String.valueOf(Integer.valueOf(tid) + 1);
            String constrt = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + tid);
            String constr = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + pid);
            JSONObject jconf = new JSONObject(constr);
            JSONObject jconft = new JSONObject(constrt);
            writeConf("tp-" + tid + KEY_FORMAT, switchConf(jconf, jconft, tid).getJSONObject("data").toString());
            getConf3("tp-" + tid + KEY_FORMAT);
            ordersNum++;
        }
    }
    private static JSONObject switchConf(JSONObject old, JSONObject oldt, String jobId) throws Exception {
        old.getJSONObject("data").put("source_host", oldt.getJSONObject("data").getString("source_host"));
        old.getJSONObject("data").put("source_charset", oldt.getJSONObject("data").getString("source_charset"));
        old.getJSONObject("data").put("source_password", oldt.getJSONObject("data").getString("source_password"));
        old.getJSONObject("data").put("source_port", oldt.getJSONObject("data").getString("source_port"));
        old.getJSONObject("data").put("source_slaveId", Long.valueOf(oldt.getJSONObject("data").getString("source_slaveId") + "746"));
        old.getJSONObject("data").put("source_user", oldt.getJSONObject("data").getString("source_user"));
        old.getJSONObject("data").put("hbase_tablename", "rbdm:" + hbaseMidName + "_" + ordersNum);
        old.getJSONObject("data").put("target.quorum", "172.19.186.89,172.19.186.90,172.19.186.91,172.19.186.93,172.19.186.93");
        //old.getJSONObject("data").put("target.quorum", "172.17.36.54,172.17.36.55,172.17.36.56");
        old.getJSONObject("data").put("target.clientport", "2181");
        old.getJSONObject("data").put("target.hbase.zkroot", "/hbase_paris");
        //old.getJSONObject("data").put("target.hbase.zkroot", "/hbase112");
        old.getJSONObject("data").put("family", "d");
        old.getJSONObject("data").put("rowkey_type", "long");
        old.getJSONObject("data").put("pool.size", "50");
        old.getJSONObject("data").put("retry.num", "2");

        //jobId config
        hbaseJobId = "tp-" + jobId + KEY_FORMAT;
        old.getJSONObject("data").put("client.name", hbaseJobId);
        old.getJSONObject("data").put("job_id", hbaseJobId);
        old.getJSONObject("data").put("monitor_job", hbaseJobId);

        JSONArray jarr = old.getJSONObject("data").getJSONArray("db_tab_meta");
        for(int i = 0; i <= jarr.length() - 1; i++) {
            JSONObject jdt = jarr.getJSONObject(i);
            String table = jdt.getString("tablename");
            jdt.put("htable", getLogicName(jdt.getString("dbname")) + "_" + getLogicName(jdt.getString("tablename"))); //dbname.eraseLast "_" and number + tablename.eraseLast "_" and number
        }
        return old;
    }
    public static String getLogicName(String name) {
        String numStr = StringUtils.substringAfterLast(name, "_");
        if(StringUtils.isNumeric(numStr))
            return StringUtils.substringBeforeLast(name, "_");
        else
            return name;
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
        //jdata.put("value", StringEscapeUtils.escapeJson(value));
        jdata.put("value", value);
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
        System.out.println(data);
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
        String addr = "http://atom.bdp.jd.local/api/site/getOrigin";
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
        String addr = "http://test.atom.bdp.jd.com/api/site/getOrigin";
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
        String addr = "http://test.atom.bdp.jd.com/api/site/getOrigin";
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
        JSONObject jret = new JSONObject(ret);
        String origin = jret.getString("obj");
        String value = StringEscapeUtils.unescapeJson(origin);
        System.out.println(new JSONObject(value));
    }
    private static void getConf3(String jobId) throws Exception {
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
        Charset charset = ContentType.getOrDefault(response.getEntity()).getCharset();
        System.out.println(response.getEntity());
        System.out.println(response.getEntity().getContent());
        //String ret = new JSONObject(new JSONTokener(new InputStreamReader(response.getEntity().getContent(), charset == null ? Charset.forName("UTF-8") : charset))).toString();
        String ret = EntityUtils.toString(response.getEntity(), enc);
        System.out.println("string = " + ret);
        String objStr = new JSONObject(ret).getString("obj");
        System.out.println(objStr);
        String objOrigin = StringEscapeUtils.unescapeJson(objStr);
        JSONObject jobj = new JSONObject(objOrigin);
        System.out.println(jobj.toString());
    }

    public String switchJob(String jobId) throws Exception {
        //getOrigin tracker jodid and parser jobid
        String tid = jobId;
        String pid = tid.replaceFirst("2002", "2102");
        String tconfStr = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + tid);
        String pconfStr = URLClient.getFromUrl(TrainBdpConfig.BDP_URL + pid);
        JSONObject tjson = new JSONObject(tconfStr);
        JSONObject pjson = new JSONObject(pconfStr);
        JSONObject tpjson = switchConf(pjson, tjson, tid);
        return tpjson.toString();
    }
}
