package com.github.hackerwin7.jlib.utils.commons;

import com.github.hackerwin7.jlib.utils.drivers.algorithm.md5.util.MD5;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * common utils for jdk
 * Created by fff on 10/28/15.
 */
public class CommonUtils {
    public static InputStream file2in(String filename, String prop) throws Exception {
        String cnf = System.getProperty(prop, "classpath:" + filename);
        InputStream in = null;
        if(cnf.startsWith("classpath:")) {
            cnf = StringUtils.substringAfter(cnf, "classpath:");
            in = CommonUtils.class.getClassLoader().getResourceAsStream(cnf);
        } else {
            in = new FileInputStream(cnf);
        }
        return in;
    }

    public static String showBytes(byte[] bytes) {
        List<String> ss = new ArrayList<>();
        for(int i = 0; i <= bytes.length - 1; i++) {
            ss.add(String.valueOf(bytes[i]));
        }
        return StringUtils.join(ss, ",");
    }

    /**
     * random generate string
     * @param length
     * @return string
     */
    public static String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int strLen = str.length();
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i <= length - 1; i++) {
            sb.append(str.charAt(random.nextInt(strLen)));
        }
        return sb.toString();
    }

    /**
     * default random string
     * @return
     */
    public static String randomString() {
        Random random = new Random();
        return randomString(random.nextInt(5) + 1);
    }

    /**
     * get md5 hex string
     * @param path
     * @return md5
     * @throws Exception
     */
    public static String md5Hex(String path) throws Exception {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }

    /**
     * fast md5 lib, but may not faster than the jdk (7, 8)
     * @param path
     * @return md5 hash hex
     * @throws Exception
     */
    public static String md5HexFast(String path) throws Exception {
        return MD5.asHex(MD5.getHash(new File(path)));
    }

    /**
     * by input stream
     * @param fis
     * @return md5
     * @throws Exception
     */
    public static String md5Hex(FileInputStream fis) throws Exception {
        return DigestUtils.md5Hex(fis);
    }

    /**
     * sha256 interface to call
     * @param path
     * @return sha hex string
     * @throws Exception
     */
    public static String sha256Hex(String path) throws Exception {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        String sha = DigestUtils.sha256Hex(fis);
        fis.close();
        return sha;
    }
}
