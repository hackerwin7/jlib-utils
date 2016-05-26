package com.github.hackerwin7.jlib.utils.test.io.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/05/25
 * Time: 4:57 PM
 * Desc: random access for read and write
 * Tips:
 */
public class RandomAccessTest {

    /* constants */
    public static final int TRANS_UNIT_SMALL = 1024;

    /* logger */
    private static final Logger LOG = Logger.getLogger(RandomAccessTest.class);

    /**
     * read file from the start to end transfer into the bytes
     * @param file
     * @param from 0 -> start, value according to whole file, equal to skip n (account) = 0 ~ n - 1 (offset)
     * @param to 0 -> end, value according to whole file
     * @throws Exception
     */
    public static byte[] file2Bytes(File file, long from, long to) throws Exception {
        //driver
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        //deal args
        long sum = file.length();
        if(from > 0)
            raf.seek(from);
        else
            from = 0;//default
        if(to <= 0)
            to = sum - 1;//default (max offset)
        int len = (int) (to - from + 1);
        if(len <= 0)
            throw new Exception("from or to offset error! from = " + from + ", to = " + to);
        //build return
        byte[] bytes = new byte[len];
        //read
        int bytesInd = 0, read = TRANS_UNIT_SMALL;
        if(len < TRANS_UNIT_SMALL)
            read = len;
        while (bytesInd <= len - 1 && raf.read(bytes, bytesInd, read) != -1) {
            bytesInd += read;
            int left = len - bytesInd;
            if(left < read)
                read = left;
        }
        raf.close();
        return bytes;
    }

    /**
     * write the bytes from src offset to end offset
     * @param bytes
     * @param file
     * @param from
     * @param to
     * @throws Exception
     */
    public static void bytes2File(byte[] bytes, File file, long from, long to) throws Exception {
        //driver
        RandomAccessFile raf = new RandomAccessFile(file, "rw"); // rwd or rws is too slowly to transfer the chunk
        //args
        long sum = bytes.length;
        if(from > 0)
            raf.seek(from);
        else
            from = 0;
        if(to <= 0)
            to = from + sum - 1;//offset position, default is until whole bytes length
        int len = (int) (to - from + 1);
        if(len <= 0)
            throw new Exception("from or to offset error! from = " + from + ", to = " + to);
        //write
        int bytesInd = 0, write = TRANS_UNIT_SMALL;
        if(len < TRANS_UNIT_SMALL)
            write = len;
        while (bytesInd <= len - 1) {
            raf.write(bytes, bytesInd, write);
            bytesInd += write;
            int left = len - bytesInd;
            if(left < write)
                write = left;
        }
        raf.close();
    }

    /**
     * main proc
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        File file = new File("src/test/resources/jlib-utils-1.0-SNAPSHOT-standalone.jar");
        File wfile = new File("src/test/resources/jlib-utils-1.0-SNAPSHOT-standalone-resume.jar");
        byte[] parrs = file2Bytes(file, 0, 1992);
        bytes2File(parrs, wfile, 0, 0);
        LOG.info("sleeping ...");
        Thread.sleep(3000);
        byte[] rarrs = file2Bytes(file, 1992, 0);
        bytes2File(rarrs, wfile, 1992, 0);
        LOG.info("md5 =>");
        LOG.info(DigestUtils.md5Hex(new FileInputStream(file)));
        LOG.info(DigestUtils.md5Hex(new FileInputStream(wfile)));
    }

    public static void test1() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(new File("src/test/resources/ip.list"), "r");
        raf.seek(3);
        LOG.info(raf.readLine());
        raf.close();
    }
}
