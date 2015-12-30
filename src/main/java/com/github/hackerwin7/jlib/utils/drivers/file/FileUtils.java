package com.github.hackerwin7.jlib.utils.drivers.file;

import com.github.hackerwin7.jlib.utils.executors.TrainBdpConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/28
 * Time: 9:51 AM
 * Desc: file utils set
 */
public class FileUtils {

    /**
     * load resource or classpath file name and convert into list
     * @param fileName resources of file name
     * @return list string
     * @throws Exception
     */
    public static List<String> file2List(String fileName) throws Exception {
        InputStream is = TrainBdpConfig.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        List<String> rows = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if(!StringUtils.isBlank(line)) {
                rows.add(line);
            }
        }
        br.close();
        is.close();
        return rows;
    }
}
