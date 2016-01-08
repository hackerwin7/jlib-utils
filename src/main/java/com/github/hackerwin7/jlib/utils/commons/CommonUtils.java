package com.github.hackerwin7.jlib.utils.commons;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
}
