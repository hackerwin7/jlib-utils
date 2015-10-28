package com.github.hackerwin7.jlib.utils.commons;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;

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
}
