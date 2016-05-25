package com.github.hackerwin7.jlib.utils.test.utils;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/15
 * Time: 4:33 PM
 * Desc:
 */
public class EscapeUtilsTest {
    public static void main(String[] args) throws Exception {
        JSONObject js = new JSONObject();
        js.put("hello", "world");
        String  jstr = js.toString();
        String jpstr = StringEscapeUtils.escapeJson(jstr);
        System.out.println(jstr);
        System.out.println(jpstr);
        String restr = StringEscapeUtils.unescapeJson(jpstr);
        String renstr = StringEscapeUtils.unescapeJson(jstr);
        System.out.println(restr);
        System.out.println(renstr);
//        JSONObject jpson = JSONObject.fromObject(jpstr);
//        System.out.println(jpson);
        JSONObject jpson = JSONObject.fromObject(jstr);
        System.out.println(jpson);
    }
}
