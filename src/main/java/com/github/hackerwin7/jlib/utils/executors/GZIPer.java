package com.github.hackerwin7.jlib.utils.executors;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/03/04
 * Time: 7:36 PM
 * Desc:
 * Tips:
 */
public class GZIPer {
    private static String encode = "utf-8";// "ISO-8859-1"

    public String getEncode() {
        return encode;
    }

    /*
     * 设置 编码，默认编码：UTF-8
     */
    public void setEncode(String encode) {
        GZIPer.encode = encode;
    }

    /*
     * 字符串压缩为字节数组
     */
    public static byte[] compressToByte(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encode));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /*
     * 字符串压缩为字节数组
     */
    public static byte[] compressToByte(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /*
     * 字节数组解压缩后返回字符串
     */
    public static String uncompressToString(byte[] b) {
        if (b == null || b.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(b);

        try {
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    /*
     * 字节数组解压缩后返回字符串
     */
    public static String uncompressToString(byte[] b, String encoding) {
        if (b == null || b.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(b);

        try {
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        GZIPer zip = new GZIPer();
        //byte[] bytes = zip.compressToByte("{\"jvr\":\"APP\",\"uid\":\"f06fba5648138d6aabe5be76aace49b5c52c0859\",\"osv\":\"8.2\",\"clt\":\"APP\",\"apc\":\"401\",\"ver\":\"1.0\",\"dvc\":\"iPhone Simultator\",\"scr\":\"640*1136\",\"chf\":\"apple_store\",\"data\":[{\"ord\":\"44444\",\"ldt\":\"12\",\"vts\":\"5\",\"ctm\":\"1427767089.238302\",\"sku\":\"13344\",\"pin\":\"123456\",\"psn\":\"3\",\"vct\":\"763267632\",\"lon\":\"11.344\",\"umd\":\"360buy\",\"utr\":\"360\",\"typ\":\"1\",\"pst\":\"32676327732\",\"net\":\"WIFI\",\"usc\":\"360buy.com\",\"par\":\"activie\",\"uct\":\"qq\",\"ads\":\"tuiguang\",\"seq\":\"0\",\"ref\":\"baidu.com\",\"ctp\":\"jd.com\",\"ucp\":\"weidian\",\"shp\":\"44343\",\"pid\":\"M8KXcglzRnY=\",\"psq\":\"6\",\"lat\":\"77.453\",\"fst\":\"2665325632\",\"key\":\"valuse\"}],\"std\":\"1\",\"bld\":\"14065\",\"osp\":\"ipad\",\"apv\":\"4.0.1\"}");

        byte[] bytes = zip.compressToByte("hello world");

//		for(byte b:bytes){
//			System.out.print(b);
//		}

        //System.out.println(HexUtils.convert(bytes));
        System.out.println(new String(bytes));
        String unzip = zip.uncompressToString(bytes);
        System.out.println(unzip);
    }
}
