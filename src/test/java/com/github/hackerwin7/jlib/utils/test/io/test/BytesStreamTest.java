package com.github.hackerwin7.jlib.utils.test.io.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/05/24
 * Time: 2:43 PM
 * Desc: read file to byte array and byte array write to file
 * Tips:
 */
public class BytesStreamTest {

    public static final int TRANS_LEN_UNIT = 1024;

    /* logger */
    private static final Logger LOG = Logger.getLogger(BytesStreamTest.class);

    /**
     * read file to bytes
     * if length is huge and more than Integer.MAX then wen can use List of byte[] and sum length class
     * @param file
     * @return bytes
     */
    public static byte[] file2BytesFrom(File file, long cp) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        try {
            if(cp > 0) {
               long skip = fis.skip(cp);
                LOG.info("skip " + skip);
            }
        } catch (IOException e) {
            LOG.error("skip " + cp + " bytes failed, error msg : " + e.getMessage(), e);
            return null;
        }
        long sum = file.length() - cp;
        if(sum > Integer.MAX_VALUE) {
            LOG.error("Too long, sum = " + sum + ", max = " + Integer.MAX_VALUE);
            return null;
        }
        byte[] arr = new byte[(int)sum];
        int indArr = 0;
        try {
            int read = 0;
            if(sum < TRANS_LEN_UNIT)
                read = (int)sum;
            else
                read = TRANS_LEN_UNIT;
            while (indArr <= sum - 1 && fis.read(arr, indArr, read) != -1) {
                indArr += read;
                int left = (int)sum - indArr;
                if(left < TRANS_LEN_UNIT)
                    read = left;
            }
            //close trans
            fis.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            LOG.error("read file io error !!!");
            return null;
        }
        return arr;
    }

    /**
     * no checkpoint file transfer
     * @param file
     * @return bytes of whole file
     */
    public static byte[] file2Bytes(File file) {
        return file2BytesFrom(file, 0);
    }

    /**
     * transfer the bytes until the ut position
     * @param file
     * @param ut
     * @return bytes
     * @throws Exception
     */
    public static byte[] file2BytesUntil(File file, long ut) throws Exception {
        byte[] arr = new byte[(int) ut];
        FileInputStream fis = new FileInputStream(file);
        int indArr = 0;
        int read = TRANS_LEN_UNIT;
        if(ut < read)
            read = (int)ut;
        while (indArr <= ut - 1 && fis.read(arr, indArr, read) != -1) {
            indArr += read;
            int left = (int)ut - indArr;
            if(left < read)
                read = left;
        }
        fis.close();
        return arr;
    }

    /**
     * !!!!!!!!!!!!!!!! RandomAccessFile will be useful for this
     * bytes to file transfer
     * @param fbytes
     */
    public static void bytes2File(byte[] fbytes, File file, long pos) throws Exception {
        FileOutputStream fos = null;
        FileChannel ch = null;
        if(pos > 0) {
            fos = new FileOutputStream(file, true);
            fos.getChannel().position(pos);
            LOG.info("pos = " + fos.getChannel().position());
        } else {
            fos = new FileOutputStream(file);
            LOG.info("pos = " + fos.getChannel().position());
        }
        int indArr = 0;
        int len = fbytes.length;
        int write = TRANS_LEN_UNIT;
        if(len < TRANS_LEN_UNIT)
            write = len;
        while (indArr <= len - 1) {
            //superposition
            fos.write(fbytes, indArr, write); // flush ?
            indArr += write;
            //adjust size of a step
            int left = len - indArr;
            if(left < write)
                write = left;
        }
        fos.close();
    }

    /**
     * without position, default is start from the end of file
     * @param fbytes
     * @param file
     * @throws Exception
     */
    public static void bytes2File(byte[] fbytes, File file) throws Exception {
        bytes2File(fbytes, file, 0);
    }

    /**
     * get md5 for a file
     * @param file
     * @return bytes
     * @throws Exception
     */
    public static byte[] getMD5(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] md5 = DigestUtils.md5(fis);
        fis.close();
        return md5;
    }

    /**
     * get md5 string for a file
     * @param file
     * @return string
     * @throws Exception
     */
    public static String getMD5Hex(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fis);// slowly
        fis.close();
        return md5;
    }

    /**
     * main test entrance
     * @param args
     */
    public static void main(String[] args) throws Exception {
        File file = new File("src/test/resources/ip.list");
        LOG.info("file = " + file.length());
        //get md5
        LOG.info("md5 = " + getMD5Hex(file));
        byte[] farrs = file2BytesUntil(file, 1992);
        LOG.info("arrays = " + farrs);
        LOG.info("farrs bytes = " + farrs.length);
        File wfile = new File("src/test/resources/ip.list.resume");
        bytes2File(farrs, wfile);
        LOG.info("wfile = " + wfile.length());
        //get md5
        LOG.info("md5 = " + getMD5Hex(wfile));

        LOG.info("sleeping ...");
        Thread.sleep(3000);

        byte[] reArrs = file2BytesFrom(file, 1992);
        LOG.info("arrays = " + reArrs);
        LOG.info(reArrs.length);
        bytes2File(reArrs, wfile, 100);
        LOG.info(wfile.length());
        //get md5
        LOG.info("md5 = " + getMD5Hex(wfile));

        LOG.info("-------------------");
        File f1 = new File("src/test/resources/ip.list");
        File f2 = new File("src/test/resources/ip.list.copy");
        File f3 = new File("src/test/resources/ip.list.resume");
        LOG.info(getMD5Hex(f1));
        LOG.info(getMD5Hex(f2));
        LOG.info(getMD5Hex(f3));
    }
}