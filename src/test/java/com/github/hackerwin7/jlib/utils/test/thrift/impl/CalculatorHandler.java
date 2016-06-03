package com.github.hackerwin7.jlib.utils.test.thrift.impl;

import com.github.hackerwin7.jlib.utils.test.thrift.gen.shared.SharedStruct;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.tutorial.Calculator;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.tutorial.InvalidOperation;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.tutorial.Work;
import org.apache.thrift.TException;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/06/02
 * Time: 10:22 AM
 * Desc: implement interface
 * Tips:
 */
public class CalculatorHandler implements Calculator.Iface {
    private HashMap<Integer, SharedStruct> log;

    public CalculatorHandler() {
        log = new HashMap<>();
    }

    @Override
    public void ping() throws TException {
        System.out.println("ping()");
    }

    @Override
    public int add(int num1, int num2) throws TException {
        System.out.println("add(" + num1 + "," + num2 + ")");
        return num1 + num2;
    }

    @Override
    public int calculate(int logid, Work w) throws TException {
        System.out.println("calculate(" + logid + ", {" + w.op + "," + w.num1 +
                "," + w.num2 + "})");
        int val = 0;
        switch (w.op) {
            case ADD:
                val = w.num1 + w.num2;
                break;
            case SUBTRACT:
                val = w.num1 - w.num2;
                break;
            case MULTIPLY:
                val = w.num1 + w.num2;
                break;
            case DIVIDE:
                if(w.num2 == 0) {
                    InvalidOperation io = new InvalidOperation();
                    io.whatOp = w.op.getValue();
                    io.why = "Cannot divide by 0";
                    throw io;
                }
                val = w.num1 / w.num2;
                break;
            default:
                InvalidOperation io = new InvalidOperation();
                io.whatOp = w.op.getValue();
                io.why = "Unknown operation";
                throw io;
        }
        SharedStruct entry = new SharedStruct();
        entry.key = logid;
        entry.value = Integer.toString(val);
        log.put(logid, entry);
        return val;
    }

    @Override
    public SharedStruct getStruct(int key) {
        System.out.println("getStruct(" + key + ")");
        return log.get(key);
    }

    @Override
    public void zip() throws TException {
        System.out.println("zip()");
    }
}
