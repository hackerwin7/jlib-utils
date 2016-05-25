package com.github.hackerwin7.jlib.utils.test.hbase.test;

import com.github.hackerwin7.jlib.utils.test.commons.CommonUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/04
 * Time: 11:11 AM
 * Desc:
 */
public class MD5HashTest {
    public static void main(String[] args) {
        long l1 = 1;
        long l2 = 2;
        System.out.println(CommonUtils.showBytes(Bytes.toBytes(MD5Hash.getMD5AsHex(Bytes.toBytes(l1)))));
        System.out.println(CommonUtils.showBytes(Bytes.toBytes(MD5Hash.getMD5AsHex(Bytes.toBytes(l2)))));
    }
}
