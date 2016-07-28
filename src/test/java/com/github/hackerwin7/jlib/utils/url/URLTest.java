package com.github.hackerwin7.jlib.utils.url;


import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/21
 * Time: 11:02 AM
 * Desc: MD5Test encode and decode and isEncoded method
 */
public class URLTest {

    public static void main(String[] args) throws Exception {
        String url = "http://ff.fff.com";
        Map<String, String> paras = new HashMap<>();
        paras.put("appid", "erp.jd.com");
        paras.put("token", "aefaiefabewufhabfe");
        paras.put("data", "jd:server:3306");
        List<NameValuePair> pairs = new ArrayList<>();
        for(Map.Entry<String, String> entry : paras.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            pairs.add(new BasicNameValuePair(key, URLEncoder.encode(val, "utf-8")));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "utf-8");
        System.out.println(URLEncodedUtils.isEncoded(entity));

        List<NameValuePair> pairs1 = new ArrayList<>();
        for(Map.Entry<String, String> entry : paras.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            pairs1.add(new BasicNameValuePair(key, val));
        }
        UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(pairs1, "utf-8");
        System.out.println(URLEncodedUtils.isEncoded(entity1));
    }
}
