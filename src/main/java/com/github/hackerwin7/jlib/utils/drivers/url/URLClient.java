package com.github.hackerwin7.jlib.utils.drivers.url;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * according to the url, read the string
 * Created by fff on 10/28/15.
 */
public class URLClient {

    /*driver*/
    private URL url = null;

    /**
     * construct with url
     * @param urlStr
     * @throws Exception
     */
    public URLClient(String urlStr) throws Exception {
        url = new URL(urlStr);
    }

    /**
     * get string data from url
     * @return string
     * @throws Exception
     */
    public String get() throws Exception {
        URLConnection uc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    /**
     * get string data from specified url
     * @param url
     * @return string data
     * @throws Exception
     */
    public static String getFromUrl(String url) throws Exception {
        URL urlOb = new URL(url);
        URLConnection uc = urlOb.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
