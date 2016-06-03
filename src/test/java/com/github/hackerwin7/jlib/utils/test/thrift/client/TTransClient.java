package com.github.hackerwin7.jlib.utils.test.thrift.client;

import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.TFileChunk;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.TFileInfo;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.TFileService;
import org.apache.log4j.Logger;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    private static final Logger LOG = Logger.getLogger(TTransClient.class);

    private static BlockingQueue<TFileChunk> queue = new LinkedBlockingQueue<>(1000);

    private static Thread writeTh = null;

    public static void main(String[] args) throws Exception {
        TTransport transport = new TSocket("localhost", 9090);
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        TFileService.Client client = new TFileService.Client(protocol);

        perform(client);

        transport.close();
    }

    private static void perform(TFileService.Client client) throws Exception {
        int fetch = 0, start = 0;

        TFileInfo info = client.open("src/test/resources/ip.list", start);

        writing();

        while (fetch < info.length) {
            TFileChunk chunk = client.getChunk();
            queue.put(chunk);
            fetch += chunk.length;
        }

        client.close();
    }

    private static void writing() {
        writeTh = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean takeAll = false;
                    RandomAccessFile raf = new RandomAccessFile(new File("src/test/resources/ip.thrift"), "rw");

                    //writing
                    while (!takeAll) {
                        TFileChunk chunk = queue.take();
                        byte[] bytes = new byte[chunk.bytes.remaining()];
                        chunk.bytes.get(bytes);

                    }

                    raf.close();
                } catch (IOException | InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        })
    }
}
