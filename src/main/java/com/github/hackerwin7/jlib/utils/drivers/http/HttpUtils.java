package com.github.hackerwin7.jlib.utils.drivers.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/14
 * Time: 10:58 AM
 * Desc: http get / post download / upload  etc...
 * Tips:
 */
public class HttpUtils {
    private static final Logger LOG = Logger.getLogger(HttpUtils.class);

    /**
     * http get request
     * @param url
     * @return response string content
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        LOG.info("executing http GET request = " + get.getRequestLine());
        CloseableHttpResponse response = client.execute(get);
        String content = response(response);
        response.close();
        client.close();
        return content;
    }

    /**
     * http post request
     * @param url
     * @param params
     * @return response string
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        LOG.info("executing http POST request = " + post.getRequestLine());
        List<NameValuePair> pairs = new ArrayList<>();
        for(Map.Entry<String, String> entry : params.entrySet()) {
            pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        post.setEntity(new UrlEncodedFormEntity(pairs));
        CloseableHttpResponse response = client.execute(post);
        String content = response(response);
        response.close();
        client.close();
        return content;
    }

    /**
     * read the response content
     * @param resp
     * @return content string
     * @throws IOException
     */
    private static String response(HttpResponse resp) throws IOException {
        StringBuilder content = new StringBuilder();
        LOG.info("response status = " + resp.getStatusLine());
        LOG.info("response code = " + resp.getStatusLine().getStatusCode());
        HttpEntity entity = resp.getEntity();
        if(entity != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            br.close();
        }
        return content.toString();
    }
}
