package com.github.hackerwin7.jlib.utils.drivers.mysql.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/25
 * Time: 3:10 PM
 * Desc: mysql meta data
 */
public class MyData {

    /*data*/
    private Map<String, MyColumn> columns = new HashMap<>();
    private String dbname = null;
    private String tbname = null;

    private MyData(Builder builder) {
        dbname = builder.dbname;
        tbname = builder.tbname;
    }

    public static Builder create() {
        return new Builder();
    }

    /*builder for mydata*/
    public static class Builder {
        private String dbname = null;
        private String tbname = null;

        public Builder dbname(String db) {
            dbname = db;
            return this;
        }

        public Builder tbname(String tb) {
            tbname = tb;
            return this;
        }

        public MyData build() {
            return new MyData(this);
        }
    }

    public void addCol(MyColumn column) {
        columns.put(column.getName(), column);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(columns);
        return sb.toString();
    }

    public Map<String, MyColumn> getColumns() {
        return columns;
    }
}
