package com.github.hackerwin7.jlib.utils.jdk.commons.test;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/18
 * Time: 2:02 PM
 * Desc:
 */
public class JsonValArrTest {
    public static void main(String[] args) throws Exception {
        String val = "{\"version.properties\":1,\"partitions\":{\"12\":[16616,18086,16635],\"8\":[16021,18082,18083],\"19\":[16783,16017,16018],\"23\":[16011,16021,16022],\"4\":[16017,16783,16784],\"15\":[16634,16013,16014],\"11\":[16614,18085,18086],\"9\":[16013,18083,18084],\"22\":[16012,16020,16021],\"13\":[16626,16011,16012],\"24\":[18083,16022,16023],\"16\":[16635,16014,16015],\"5\":[16018,16784,16785],\"10\":[16023,18084,18085],\"21\":[16785,16019,16020],\"6\":[16019,16785,18081],\"1\":[16014,16635,16781],\"17\":[16781,16015,16016],\"14\":[16628,16012,16013],\"0\":[16022,16023,16011],\"20\":[16784,16018,16019],\"2\":[16015,16781,16782],\"18\":[16782,16016,16017],\"7\":[16020,18081,18082],\"3\":[16016,16782,16783]}}";
        JSONObject jdata = JSONObject.fromObject(val);
        JSONObject jpart = jdata.getJSONObject("partitions");
        System.out.println(jpart.get("12"));
        JSONArray jarr = jpart.getJSONArray("12");
        for(int i = 0; i <= jarr.size() - 1; i++) {
            int num = jarr.getInt(i);
            System.out.println(num);
        }
    }
}
