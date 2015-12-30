package com.github.hackerwin7.jlib.utils.drivers.mysql.data;

import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/28
 * Time: 11:24 AM
 * Desc: mydata column
 */
public class MyColumn {

    private String name = null;
    private String mtype = null;
    private String value = null;
    private String jtype = null;
    private int size = 0;
    private String ext = null;

    private MyColumn(Builder builder) {
        name = builder.name;
        mtype = builder.mtyoe;
        value = builder.value;
        jtype = builder.jtype;
        size = builder.size;
        ext = builder.ext;
    }

    public static Builder create() {
        return new Builder();
    }

    public static String getValue(ResultSet rs, String name, String jtype) throws Exception {
        if(StringUtils.containsIgnoreCase(jtype, "String")) {
            return String.valueOf(rs.getString(name));
        } else if(StringUtils.containsIgnoreCase(jtype, "Timestamp")) {
            return String.valueOf(rs.getTimestamp(name));
        } else if(StringUtils.containsIgnoreCase(jtype, "Integer")) {
            return String.valueOf(rs.getInt(name));
        } else if(StringUtils.containsIgnoreCase(jtype, "Long")) {
            return String.valueOf(rs.getLong(name));
        } else if(StringUtils.containsIgnoreCase(jtype, "BigDecimal")) {
            return String.valueOf(rs.getBigDecimal(name));
        } else {//default
            return String.valueOf(rs.getObject(name));
        }
    }

    public static class Builder {
        private String name = null;
        private String mtyoe = null;
        private String value = null;
        private String jtype = null;
        private int size = 0;
        private String ext = null;

        private Builder() {

        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder mtype(String type) {
            this.mtyoe = type;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder jtype(String jtype) {
            this.jtype = jtype;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder ext(String ext) {
            this.ext = ext;
            return this;
        }

        public MyColumn build() {
            return new MyColumn(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getMtype() {
        return mtype;
    }

    public String getValue() {
        return value;
    }

    public String getJtype() {
        return jtype;
    }

    public int getSize() {
        return size;
    }

    public String getExt() {
        return ext;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("| name : " + name)
                .append("| value : " + value)
                .append("| jtype : " + jtype)
                .append("| mtype : " + mtype)
                .append("| size : " + size)
                .append("| ext : " + ext);
        return sb.toString();
    }
}
