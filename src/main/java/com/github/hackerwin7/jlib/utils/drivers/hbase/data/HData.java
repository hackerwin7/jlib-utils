package com.github.hackerwin7.jlib.utils.drivers.hbase.data;

import com.github.hackerwin7.jlib.utils.commons.wrap.ByteArrayWrapper;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/23
 * Time: 2:43 PM
 * Desc: hbase data for hbase client, include single rowkey, multiple family, multiple * multiple qualifier, mul * mul value
 */
public class HData {
    private byte[] rowkey = null;
    private Map<ByteArrayWrapper, Set<HValue>> vals = new HashMap<>(); // key = family, value = <qualifier, value>

    /* qualifier and value, override equals and hash code */
    public class HValue {
        private final byte[] qualifier;
        private final byte[] value;

        public HValue(byte[] qualifier, byte[] value) {
            this.qualifier = qualifier;
            this.value = value;
        }

        @Override
        public boolean equals(Object other) {
            if(other instanceof HValue)
                return Arrays.equals(qualifier, ((HValue) other).getQualifier());
            else
                return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(qualifier);
        }

        public byte[] getQualifier() {
            return qualifier;
        }

        public byte[] getValue() {
            return value;
        }
    }

    /**
     * default constructor
     * @param rowkey
     */
    public HData(byte[] rowkey) {
        this.rowkey = rowkey;
    }

    /**
     * get rowkey
     * @return rowkey
     */
    public byte[] getRowkey() {
        return rowkey;
    }

    /**
     * exist family
     * @param family
     * @return bool
     */
    public boolean exist(byte[] family) {
        return vals.containsKey(new ByteArrayWrapper(family));
    }

    /**
     * exist family, qualifier
     * @param family
     * @param qualifier
     * @return bool
     */
    public boolean exist(byte[] family, byte[] qualifier) {
        if(!exist(family))
            return false;
        return vals.get(new ByteArrayWrapper(family)).contains(new HValue(qualifier, null));
    }

    /**
     * add family
     * @param family
     */
    public void add(byte[] family) {
        ByteArrayWrapper baw = new ByteArrayWrapper(family);
        if(!vals.containsKey(baw))
            vals.put(baw, new HashSet<HValue>());
    }

    /**
     * add a columnByteArrayWrapper(qualifier)
     * @param family
     * @param qualifier
     * @param value
     */
    public void add(byte[] family, byte[] qualifier, byte[] value) {
        ByteArrayWrapper baw = new ByteArrayWrapper(family);
        if(!exist(family))
            add(family);
        vals.get(baw).add(new HValue(qualifier, value));
    }

    /**
     * get multiple columns
     * @param family
     * @return HValues
     */
    public Set<HValue> getColumns(byte[] family) {
        return vals.get(new ByteArrayWrapper(family));
    }

    /**
     * get list of family
     * @return families
     */
    public List<byte[]> getFamilyList() {
        List<byte[]> families = new ArrayList<>();
        for(Map.Entry<ByteArrayWrapper, Set<HValue>> entry : vals.entrySet()) {
            families.add(entry.getKey().getData());
        }
        return families;
    }
}
