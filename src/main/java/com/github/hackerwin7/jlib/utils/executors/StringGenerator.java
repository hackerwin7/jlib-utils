package com.github.hackerwin7.jlib.utils.executors;

import com.github.hackerwin7.jlib.utils.drivers.file.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/17
 * Time: 2:05 PM
 * Desc:
 */
public class StringGenerator {
    public static void main(String[] args) throws Exception {
        proc3();
    }

    public static void proc1() {
        String sku = "sku_";
        List<String> tbs = new LinkedList<>();
        for(int i = 1; i <= 64; i++) {
            tbs.add(sku + i);
        }
        System.out.println(StringUtils.join(tbs, ","));
    }

    public static void proc2() {
        String instr = "1000174,1000176,1000178,1000180,1000182,1000184,1000186,1000188,1000190,1000192,1000194,1000196,1000198,1000200,1000202,1000204,1000206,1000208,1000210,1000212";
        String[] strs = StringUtils.split(instr, ",");
        for(String id : strs) {
            System.out.println("create /checkpoint/" + "tp-" + id + SwitchTpConfig.KEY_FORMAT_TEST + " init");
            System.out.println("create /checkpoint/" + "tp-" + id + SwitchTpConfig.KEY_FORMAT_TEST + "/TP init");
        }
    }

    public static void proc3() throws Exception {
        List<String> types = FileUtils.file2List("types.list");
        for(String type : types) {
            System.out.println("case " + type + ":");
            System.out.println("    return \"" + type + "\";");
        }
    }
}
