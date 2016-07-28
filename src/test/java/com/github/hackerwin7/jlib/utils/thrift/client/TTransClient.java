package com.github.hackerwin7.jlib.utils.thrift.client;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.thrift.gen.trans.TFileChunk;
import com.github.hackerwin7.jlib.utils.thrift.gen.trans.TFileInfo;
import com.github.hackerwin7.jlib.utils.thrift.gen.trans.TFileService;
import org.apache.log4j.Logger;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/06/03
 * Time: 4:38 PM
 * Desc: client for trans thrift
 * Tips:
 */
public class TTransClient {

    /* constants */
    public static final String JAR_PATH = "src/test/resources/";

    private static final Logger LOG = Logger.getLogger(TTransClient.class);

    private static BlockingQueue<TFileChunk> queue = new LinkedBlockingQueue<>(1000);

    private static Thread writeTh = null;

    private TFileInfo info = null;

    public static void main(String[] args) throws Exception {
        TTransport transport = new TSocket("localhost", 9090);
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        TFileService.Client client = new TFileService.Client(protocol);

        TTransClient tClient = new TTransClient();
        tClient.perform(client);

        transport.close();
    }

    private void perform(TFileService.Client client) throws Exception {
        int fetch = 0, start = 0;

        info = client.open("ip.list", start);

        // debug
        TFileChunk tChunk = client.getTChunk();
        byte[] cbytes = tChunk.getBytes();

        System.out.println(new String(cbytes));
        System.out.println(tChunk.name);

        writing();

        while (fetch < info.length) {
            TFileChunk chunk = client.getChunk();
            if(chunk != null) {
                queue.put(chunk);
                fetch += chunk.length;
            }
        }

        checking();

        client.close();
    }

    private void writing() throws Exception {
        writeTh = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean takeAll = false;
                    RandomAccessFile raf = new RandomAccessFile(new File(JAR_PATH + "ip.thrift"), "rw");
                    long write = 0;

                    //writing
                    while (!takeAll) {
                        TFileChunk chunk = queue.take();
                        byte[] wbytes = chunk.getBytes();
                        raf.write(wbytes, 0, (int)chunk.length);
                        write += chunk.length;
                        if(write == info.length)
                            takeAll = true; //all chunks haven been taken
                    }

                    raf.close();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
        writeTh.start();
    }

    private void checking() throws Exception {
        writeTh.join();
        System.out.println("src: " + info.md5);
        System.out.println("des: " + CommonUtils.md5Hex(JAR_PATH + "ip.thrift"));
    }
}
