package com.github.hackerwin7.jlib.utils.commons;

import org.apache.commons.lang3.StringUtils;

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
}
