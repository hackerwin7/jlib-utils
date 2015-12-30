package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.file.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/25
 * Time: 4:01 PM
 * Desc: process string
 */
public class RowStringProcess {
    public static void main(String[] args) throws Exception {
        RowStringProcess rsp = new RowStringProcess();
        rsp.mainProc();
    }

    public void mainProc() throws Exception {
        List<String> fields = FileUtils.file2List("row.list");
        List<String> types = FileUtils.file2List("row1.list");
        for(int i = 0; i <= fields.size() - 1; i++) {
            String name = fields.get(i);
            String type = types.get(i);
            String show = null;
            if(StringUtils.containsIgnoreCase(type, "bigint")) {
                show = "rs.getLong(\"" + name + "\")";
            } else if(StringUtils.containsIgnoreCase(type, "date")) {
                show = "rs.getTimestamp(\"" + name + "\")";
            } else if(StringUtils.containsIgnoreCase(type, "decimal")) {
                show = "rs.getBigDecimal(\"" + name + "\")";
            } else {
                show = "rs.getString(\"" + name + "\")";
            }
            String out = "values.put(\"" + name + "\", String.valueOf("+ show +"));";
            String out1 = "data.addCol(MyColumn.create().name(\"" + name + "\").jtype(\"" + type + "\").value(String.valueOf(" + show + ")).build());";
            System.out.println(out1);
        }
    }
}
