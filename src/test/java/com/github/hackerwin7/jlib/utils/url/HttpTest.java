package com.github.hackerwin7.jlib.utils.url;

import com.github.hackerwin7.jlib.utils.drivers.http.HttpUtils;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/14
 * Time: 10:22 AM
 * Desc:
 * Tips:
 */
public class HttpTest {
    public static void main(String[] args) {
        HttpTest ht = new HttpTest();
        ht.test1();
    }

    public void test() {
        try {
            String call = HttpUtils.get("http://localhost:8080/servlet-maven-test/config?id=call");
            JSONObject jcall = JSONObject.fromObject(call);
            System.out.println("json = " + jcall);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void test1() {
        try {
            String call = HttpUtils.get("http://localhost:8080/servlet-maven-test/config?id=call");
            JSONObject jcall = JSONObject.fromObject(call);
            System.out.println("get = " + jcall);
            jcall.put("id", "version.properties");
            jcall.put("group", "sayi");
            jcall.getJSONObject("data").put("name", "version.properties");
            jcall.getJSONObject("data").put("age", 67);
            jcall.getJSONObject("data").put("email", "tomcat@qq.com");

            Map<String, String> params = new HashMap<>();
            params.put("config", jcall.toString());
            System.out.println("post = " + jcall);
            String ret = HttpUtils.post("http://localhost:8080/servlet-maven-test/conf", params);
            System.out.println(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
