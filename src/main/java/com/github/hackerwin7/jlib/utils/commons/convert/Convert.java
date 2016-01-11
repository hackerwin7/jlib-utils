package com.github.hackerwin7.jlib.utils.commons.convert;

import com.github.hackerwin7.jlib.utils.drivers.hbase.data.HData;
import com.github.hackerwin7.jlib.utils.drivers.mysql.data.MyColumn;
import com.github.hackerwin7.jlib.utils.drivers.mysql.data.MyData;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/11
 * Time: 10:54 AM
 * Desc: convert mtype class
 */
public class Convert {

    /*logger*/
    private static Logger logger = Logger.getLogger(Convert.class);

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

    /**
     * file convert to input stream
     * @param filename
     * @param prop
     * @return input stream
     * @throws Exception
     */
    public static InputStream file2in(String filename, String prop) throws Exception {
        String cnf = System.getProperty(prop, "classpath:" + filename);
        InputStream in = null;
        if(cnf.startsWith("classpath:")) {
            cnf = StringUtils.substringAfter(cnf, "classpath:");
            in = Convert.class.getClassLoader().getResourceAsStream(cnf);
        } else {
            in = new FileInputStream(cnf);
        }
        return in;
    }

    /**
     * convert hdata to put
     * @param data
     * @return put
     */
    public static Put hdata2put(HData data) {
        Put put = new Put(data.getRowkey());
        List<byte[]> families = data.getFamilyList();
        for(byte[] family : families) {
            Set<HData.HValue> values = data.getColumns(family);
            for(HData.HValue value : values) {
                put.addColumn(family, value.getQualifier(), value.getValue());
            }
        }
        return put;
    }
}
