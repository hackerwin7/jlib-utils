package com.github.hackerwin7.jlib.utils.commons.wrap;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/11
 * Time: 3:37 PM
 * Desc: a wrapper for byte[]
 */
public class ByteArrayWrapper {

    /* data */
    private final byte[] data;

    /**
     * constructor with byte[]
     * @param data
     */
    public ByteArrayWrapper(byte[] data){
        this.data = data;
    }

    /**
     * override the equal method
     * @param other
     * @return bool
     */
    @Override
    public boolean equals(Object other) {
        if(other instanceof ByteArrayWrapper) {
            return Arrays.equals(data, ((ByteArrayWrapper) other).getData());
        } else {
            return false;
        }
    }

    /**
     * override the hashcode for something else use, such as HashMap key
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    /**
     * getOrigin data
     * @return
     */
    public byte[] getData() {
        return data;
    }
}
