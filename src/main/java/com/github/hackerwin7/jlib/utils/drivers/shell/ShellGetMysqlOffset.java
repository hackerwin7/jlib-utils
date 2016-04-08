package com.github.hackerwin7.jlib.utils.drivers.shell;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/03/08
 * Time: 2:12 PM
 * Desc: get the mysql show master status from the shell client
 * Tips:
 */
public class ShellGetMysqlOffset {



    /* simple offset for mysql */
    public static class MysqlSimpleOffset {
        public void setLogFileName(String logFileName) {
            this.logFileName = logFileName;
        }

        public void setLogFileOffset(long logFileOffset) {
            this.logFileOffset = logFileOffset;
        }

        public String getLogFileName() {
            return logFileName;
        }

        public long getLogFileOffset() {
            return logFileOffset;
        }

        public String toString() {
            return logFileName + ":" + logFileOffset;
        }

        private String logFileName = null;
        private long logFileOffset = 0;
    }

    /**
     * execute a shell command "show master status" on the specific mysql source
     * @param ip
     * @return offset
     * @throws Exception
     */
    public static MysqlSimpleOffset showMasterStatus(String ip) throws Exception {
        MysqlSimpleOffset offset = new MysqlSimpleOffset();
        String cmd = "ssh magpie@172.22.178.176 \"mysql -h" + ip + " -ucanal -pFdHTbSjheGVNQwEDNSXXOO_D2efdsdF -P3358 -e \'show master status;\'\"";
        List<String> rets = ShellClient.execute(cmd);
        if(rets.size() != 2) {
            cmd = "ssh magpie@172.22.178.176 \"mysql -h" + ip + " -umagpie -pFdHTbSjheGVNQwEDNSXXOO_D2efdsdF -P3358 -e \'show master status;\'\"";
            rets = ShellClient.execute(cmd);
        }
        String[] strArr = StringUtils.split(rets.get(1), "\t");
        offset.setLogFileName(strArr[0]);
        offset.setLogFileOffset(Long.parseLong(strArr[1]));
        return offset;
    }

    /**
     * test static  class
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        MysqlSimpleOffset offset = showMasterStatus("10.191.66.89");
        System.out.println(offset);
        MysqlSimpleOffset offset1 = showMasterStatus("172.19.148.68");
        System.out.println(offset + " " + offset1);
    }
}
