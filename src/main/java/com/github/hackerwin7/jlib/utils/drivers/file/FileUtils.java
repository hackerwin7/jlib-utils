package com.github.hackerwin7.jlib.utils.drivers.file;

import com.github.hackerwin7.jlib.utils.executors.TrainBdpConfig;
import org.apache.commons.lang3.StringUtils;
import org.ho.yaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * load the specified class loader
     * @param fileName
     * @param loader
     * @return list string
     * @throws Exception
     */
    public static List<String> file2List(String fileName, ClassLoader loader) throws Exception {
        InputStream is = loader.getResourceAsStream(fileName);
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

    /**
     * load the specified class loader
     * @param fileName
     * @param stream
     * @return list string
     * @throws Exception
     */
    public static List<String> file2List(String fileName, InputStream stream) throws Exception {
        InputStream is = stream;
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

    /**
     * read yaml file and return the value
     * @param fileName
     * @param key
     * @return value string
     * @throws Exception
     */
    public static String readYaml(String fileName, String key) throws Exception {
        HashMap map = Yaml.loadType(FileUtils.class.getClassLoader().getResource(fileName).openStream(), HashMap.class);
        return map.get(key).toString();
    }

    /**
     * delete recursively if file is directory
     * @param file
     * @throws IOException
     */
    public static void delete(File file) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(file);
    }
}
