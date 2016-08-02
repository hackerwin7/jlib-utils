package com.github.hackerwin7.jlib.utils.md5;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.drivers.algorithm.md5.util.MD5;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/08/02
 * Time: 2:13 PM
 * Desc:
 * Tips:
 */
public class FastMd5Test {

    public static void main(String[] args) throws Exception {
        FastMd5Test fmt = new FastMd5Test();
        fmt.run();
    }

    public void run() throws Exception {
        long fastStart = System.currentTimeMillis();
        String fastHash = MD5.asHex(MD5.getHash(new File("src/test/resources/protobuf-3.0.0-beta-2.tar.gz")));
        long fastEnd = System.currentTimeMillis();
        long normalStart = System.currentTimeMillis();
        String normalHash = CommonUtils.md5Hex("src/test/resources/protobuf-3.0.0-beta-2.tar.gz");
        long normalEnd = System.currentTimeMillis();
//        long shaStart = System.currentTimeMillis();
//        String shaHash = CommonUtils.sha256Hex("src/test/resources/protobuf-3.0.0-beta-2.tar.gz");//so slowly
//        long shaEnd = System.currentTimeMillis();
        System.out.println(fastHash + ":" + (fastEnd - fastStart));
        System.out.println(normalHash + ":" + (normalEnd - normalStart));
//        System.out.println(shaHash + ":" + (shaEnd - shaStart));
    }
}
