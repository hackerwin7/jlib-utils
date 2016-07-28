package com.github.hackerwin7.jlib.utils.thrift.client;

import com.github.hackerwin7.jlib.utils.thrift.gen.shared.SharedStruct;
import com.github.hackerwin7.jlib.utils.thrift.gen.tutorial.Calculator;
import com.github.hackerwin7.jlib.utils.thrift.gen.tutorial.InvalidOperation;
import com.github.hackerwin7.jlib.utils.thrift.gen.tutorial.Operation;
import com.github.hackerwin7.jlib.utils.thrift.gen.tutorial.Work;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/05/31
 * Time: 4:08 PM
 * Desc: implement example for the thrift tutorial
 * Tips:
 */
public class ThriftClientTest {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Please enter 'simple' or 'secure'");
            System.exit(0);
        }

        try {
            TTransport transport;
            if(args[0].contains("simple")) {
                transport = new TSocket("localhost", 9090);
                transport.open();
            } else {
                TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
                params.setTrustStore("src/test/resources/clienttruststore", "hacker", "SunX509", "JKS");
                transport = TSSLTransportFactory.getClientSocket("localhost", 9091, 0, params);
            }

            TProtocol protocol = new TBinaryProtocol(transport);
            Calculator.Client client = new Calculator.Client(protocol);

            perform(client);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(Calculator.Client client) throws TException {
        client.ping();
        System.out.println("ping()");

        int sum = client.add(1, 1);
        System.out.println("1+1=" + sum);

        Work work = new Work();

        work.op = Operation.DIVIDE;
        work.num1 = 1;
        work.num2 = 0;
        try {
            int quotient = client.calculate(1, work);
            System.out.println("Whoa we can divide by 0");
        } catch (InvalidOperation io) {
            System.out.println("Invalid operation: " + io.why);
        }

        work.op = Operation.SUBTRACT;
        work.num1 = 16;
        work.num2 = 10;
        try {
            int diff = client.calculate(1, work);
            System.out.println("16-10=" + diff);
        } catch (InvalidOperation io) {
            System.out.println("Invalid operation: " + io.why);
        }

        SharedStruct log = client.getStruct(1);
        System.out.println("Check log: " + log.value);
    }
}
