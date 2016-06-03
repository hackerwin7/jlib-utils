package com.github.hackerwin7.jlib.utils.test.thrift.server;

import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.TFileService;
import com.github.hackerwin7.jlib.utils.test.thrift.impl.TFileServiceHandler;
import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/06/03
 * Time: 4:30 PM
 * Desc: server for file trans thrift
 * Tips:
 */
public class TTransServer {

    private static final Logger LOG = Logger.getLogger(TTransServer.class);

    public static TFileServiceHandler handler;

    public static TFileService.Processor processor;

    public static void main(String[] args) {
        handler = new TFileServiceHandler();
        processor = new TFileService.Processor(handler);

        Runnable simple = new Runnable() {
            @Override
            public void run() {
                simple(processor);
            }
        };

        new Thread(simple).start();
    }

    public static void simple(TFileService.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            LOG.info("starting simple server ......");
            server.serve();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
