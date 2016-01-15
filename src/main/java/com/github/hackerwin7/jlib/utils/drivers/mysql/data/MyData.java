package com.github.hackerwin7.jlib.utils.drivers.mysql.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/01/12
 * Time: 4:52 PM
 * Desc:  mysql data for mysql client
 */
public class MyData {

    /* data */
    private Map<String, Column> columns = new HashMap<>();
    private String ip = null;
    private String port = null;
    private String dbname = null;
    private String tbname = null;

    public void setColumn(Column column) {
        columns.put(column.getName(), column);
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public void setTbname(String tbname) {
        this.tbname = tbname;
    }

    public Column getColumn(String name) {
        return columns.get(name);
    }

    public List<Column> getColumnList() {
        List<Column> columnList = new ArrayList<>();
        for(Map.Entry<String, Column> entry : columns.entrySet()) {
            columnList.add(entry.getValue());
        }
        return columnList;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getDbname() {
        return dbname;
    }

    public String getTbname() {
        return tbname;
    }

    /* class */
    public static class Column {
        private String name = null;
        private String value = null;
        private boolean isKey = false;
        private boolean isNull = false;
        private String sqlType = null;
        private String javaType = null;
        private int length = 0;

        private Column(Builder builder) {
            name = builder.name;
            value = builder.value;
            isKey = builder.isKey;
            isNull = builder.isNull;
            sqlType = builder.sqlType;
            javaType = builder.javaType;
            length = builder.length;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public static class Builder {
            private String name = null;
            private String value = null;
            private boolean isKey = false;
            private boolean isNull = false;
            private String sqlType = null;
            private String javaType = null;
            private int length = 0;

            private Builder() {

            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder value(String value) {
                this.value = value;
                return this;
            }

            public Builder isKey(boolean isKey) {
                this.isKey = isKey;
                return this;
            }

            public Builder isNull(boolean isNull) {
                this.isNull = isNull;
                return this;
            }

            public Builder sqlType(String sqlType) {
                this.sqlType = sqlType;
                return this;
            }

            public Builder javaType(String javaType) {
                this.javaType = javaType;
                return this;
            }

            public Builder length(int length) {
                this.length = length;
                return this;
            }

            public Column build() {
                return new Column(this);
            }
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isKey() {
            return isKey;
        }

        public boolean isNull() {
            return isNull;
        }

        public String getSqlType() {
            return sqlType;
        }

        public String getJavaType() {
            return javaType;
        }

        public int getLength() {
            return length;
        }
    }
}
