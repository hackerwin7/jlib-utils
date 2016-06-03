package com.github.hackerwin7.jlib.utils.test.thrift.server;

import com.github.hackerwin7.jlib.utils.test.thrift.impl.CalculatorHandler;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.tutorial.Calculator;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/06/02
 * Time: 10:16 AM
 * Desc: thrift tutorial for server
 * Tips:
 */
public class ThriftServerTest {

    public static CalculatorHandler handler;

    public static Calculator.Processor processor;

    public static void main(String[] args) {
        try {
            handler = new CalculatorHandler();
            processor = new Calculator.Processor(handler);

            Runnable simple = new Runnable() {
                @Override
                public void run() {
                    simple(processor);
                }
            };
            Runnable secure = new Runnable() {
                @Override
                public void run() {
                    secure(processor);
                }
            };

            new Thread(simple).start();
            new Thread(secure).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(Calculator.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void secure(Calculator.Processor processor) {
        try {
            TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
            params.setKeyStore("src/test/resources/clientkeystore", "hacker", null, null);
            TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(9091, 0, null, params);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting the secure server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}