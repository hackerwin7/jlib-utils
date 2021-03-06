package com.github.hackerwin7.jlib.utils.commons.convert;

import com.github.hackerwin7.jlib.utils.drivers.hbase.data.HData;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/11
 * Time: 10:54 AM
 * Desc: convert mtype class
 */
public class Convert {

    /* logger */
    private static Logger logger = Logger.getLogger(Convert.class);

    /* constans */
    public static final int READ_BUFFER_UNIT = 2048;

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

    /**
     * convert a result into a hdata
     * Single row result of a Get or Scan query.
     * @param result
     * @return hdata
     */
    public static HData result2hdata(Result result) throws Exception {
        HData data = new HData(result.getRow());
        for(Cell cell : result.rawCells()) {
            byte[] rowkey = CellUtil.cloneRow(cell);
            if(!Arrays.equals(rowkey, data.getRowkey())) {
                throw new Exception("cell's rowkey != result.gerRow()");
            }
            byte[] family = CellUtil.cloneFamily(cell);
            byte[] qualifier = CellUtil.cloneQualifier(cell);
            byte[] value = CellUtil.cloneValue(cell);
            data.add(family, qualifier, value);
        }
        return data;
    }

    /**
     * bytes to string
     * @param bytes
     * @return string
     * @throws Exception
     */
    public static String bytes2String(byte[] bytes) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Integer> nums = new ArrayList<>();
        for(int i = 0; i <= bytes.length - 1; i++) {
            nums.add((int) bytes[i]);
        }
        sb.append(StringUtils.join(nums, ","));
        return sb.toString();
    }

    /**
     * input stream to string
     * @param is
     * @return string
     */
    public static String stream2String(InputStream is) {
        char[] buffer = new char[READ_BUFFER_UNIT];
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new InputStreamReader(is, "UTF-8")) {
            while (true) {
                int readSize = reader.read(buffer, 0, buffer.length);
                if(readSize < 0)
                    break;
                sb.append(buffer, 0, readSize);
                buffer = new char[READ_BUFFER_UNIT]; // key note: new storage for next reading
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return sb.toString();
    }

    /**
     * start a thread to asynchronously convert the strea to stream builder
     * @param is
     * @param sb
     * @return a alive processing thread
     */
    public static Thread stream2StringBuilderAsync(final InputStream is, final StringBuilder sb) {
        Thread proc = new Thread(new Runnable() {
            @Override
            public void run() {
                sb.append(stream2String(is));
            }
        });
        proc.start();
        return proc;
    }
}
