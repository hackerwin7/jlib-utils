package com.github.hackerwin7.jlib.utils.drivers.url;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/15
 * Time: 10:32 AM
 * Desc: get or post to request and response
 */
public class HttpClient {
    /*logger*/
    private static Logger logger = Logger.getLogger(HttpClient.class);
    /*driver*/
    private DefaultHttpClient client = null;
    /*constants*/
    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * constructor
     */
    public HttpClient() {
        client = new DefaultHttpClient();
    }

    /**
     * connect
     */
    public void connect() {
        client = new DefaultHttpClient();
    }

    /**
     * http get
     * @param url
     * @return string
     */
    public String get(String url) throws Exception {
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    }

    /**
     * http post
     * @param url
     * @return string
     * @throws Exception
     */
    public String post(String url, Map<String, String> params) throws Exception {
        HttpPost request = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<>();
        for(Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            pairs.add(new BasicNameValuePair(key, val));
        }
        request.setEntity(new UrlEncodedFormEntity(pairs, DEFAULT_CHARSET));
        HttpResponse response = client.execute(request);
        return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
    }

    /**
     * close the http client
     */
    public void disconnect() {
        if(client != null)
            client.getConnectionManager().shutdown();
    }
}
