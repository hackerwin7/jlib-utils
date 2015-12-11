package com.github.hackerwin7.jlib.utils.utils.convert;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/11
 * Time: 10:54 AM
 * Desc: convert type class
 */
public class Convert {

    /*logger*/
    private static Logger logger = Logger.getLogger(Convert.class);

    /**
     * json to map
     * @param json
     * @return
     */
    public static Map json2MapManual(JSONObject json) {
        Map map = new HashMap();
        Iterator keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            String val = json.get(key).toString();
            if(val.startsWith("{") && val.endsWith("}")) { // value is jsonObject
                map.put(key, json2MapManual(JSONObject.fromObject(val)));
            } else {
                map.put(key, val);
            }
        }
        return map;
    }

    /**
     * json convert to map using ObjectMapper
     * @param json
     * @return
     */
    public static Map json2Map(JSONObject json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(json.toString(), Map.class);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * map convert to json
     * @param map
     * @return
     */
    public static JSONObject map2Json(Map map) {
        return JSONObject.fromObject(map);
    }
}
