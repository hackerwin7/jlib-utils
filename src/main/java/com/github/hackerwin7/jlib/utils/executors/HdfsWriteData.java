package com.github.hackerwin7.jlib.utils.executors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.log4j.Logger;

/**
 * Created by fff on 11/6/15.
 */
public class HdfsWriteData {

    private static Logger logger = Logger.getLogger(HdfsWriteData.class);

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", LocalFileSystem.class.getName());
        System.out.println(DistributedFileSystem.class.getName());
        System.out.println(LocalFileSystem.class.getName());
        Path path = new Path("hdfs://192.168.144.110:9000/tmp/test.log");
        FileSystem fs = path.getFileSystem(conf);
        FSDataOutputStream out = fs.create(path);
        logger.info("writing data");
        out.write("no data in hdfs\t\n".getBytes("UTF-8"));
        out.flush();
    }
}
