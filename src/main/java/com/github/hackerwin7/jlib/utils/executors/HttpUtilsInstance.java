package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.http.HttpUtils;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/14
 * Time: 6:00 PM
 * Desc:
 * Tips:
 */
public class HttpUtilsInstance {
    public static void main(String[] args) throws IOException {
        String mapping = args[0];
        String id = args[1];
        String name = "config";
        String val = args[2];
        Map<String, String> params = new HashMap<>();
        JSONObject jval = new JSONObject();
        jval.put("id", id);
        jval.put("data", val);
        params.put(name, jval.toString());
        String ret = HttpUtils.post("http://localhost:8080/servlet-maven-test/" + mapping, params);
        System.out.println(ret);
    }
}
