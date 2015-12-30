package com.github.hackerwin7.jlib.utils.drivers.hbase.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/23
 * Time: 2:43 PM
 * Desc: hbase data for hbase client
 */
public class HData {
    private byte[] rowkey;
    @Deprecated
    private byte[] family;
    @Deprecated
    private byte[] qualifier;
    @Deprecated
    private byte[] value;
    private String tablename;
    private String namespace;

    private List<HValue> values = new ArrayList<>();

    public void addValue(HValue value) {
        values.add(value);
    }

    public class HValue {
        private byte[] family;
        private byte[] qualifier;
        private byte[] value;

        public HValue(byte[] family, byte[] qualifier, byte[] value) {
            this.family = family;
            this.qualifier = qualifier;
            this.value = value;
        }


        public byte[] getFamily() {
            return family;
        }

        public byte[] getQualifier() {
            return qualifier;
        }

        public byte[] getValue() {
            return value;
        }

    }

    private HData(HData.Builder builder) {
        rowkey = builder.rowkey;
        family = builder.family;
        qualifier = builder.qualifier;
        value = builder.value;
        tablename = builder.tablename;
        namespace = builder.namespace;
        values.addAll(builder.values);
    }

    public static HData.Builder create() {
        return new Builder();
    }

    public static class Builder {
        private byte[] rowkey;
        @Deprecated
        private byte[] family;
        @Deprecated
        private byte[] qualifier;
        @Deprecated
        private byte[] value;
        private String tablename;
        private String namespace;

        private List<HValue> values = new ArrayList<>();

        private Builder() {

        }

        public Builder rowkey(byte[] rowkey) {
            this.rowkey = rowkey;
            return this;
        }

        @Deprecated
        public Builder family(byte[] family) {
            this.family = family;
            return this;
        }

        @Deprecated
        public Builder qualifier(byte[] qualifier) {
            this.qualifier = qualifier;
            return this;
        }

        @Deprecated
        public Builder value(byte[] value) {
            this.value = value;
            return this;
        }

        public Builder tablename(String tablename) {
            this.tablename = tablename;
            return this;
        }

        public Builder value(HValue value) {
            values.add(value);
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public HData build() {
            return new HData(this);
        }
    }

    public byte[] getRowkey() {
        return rowkey;
    }

    @Deprecated
    public byte[] getFamily() {
        return family;
    }

    @Deprecated
    public byte[] getQualifier() {
        return qualifier;
    }

    @Deprecated
    public byte[] getValue() {
        return value;
    }

    public String getTablename() {
        return tablename;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<HValue> getValues() {
        return values;
    }
}
