package com.github.hackerwin7.jlib.utils.thread;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/08/05
 * Time: 11:24 AM
 * Desc:
 * Tips:
 */
public class SynchronizedTest {

    private List<String> list = new LinkedList<>();

    public synchronized void syncAdd(String str) {
        System.out.println("sync adding " + str);
        list.add(str);
        System.out.println("sync added " + str);
    }

    public void add(String str) {
        System.out.println("adding " + str);
        list.add(str);
        System.out.println("added " + str);
    }

    public static void main(String[] args) throws Exception {
        SynchronizedTest st = new SynchronizedTest();
        st.start();
    }

    public void start() throws Exception {
        Thread sync = new Thread(new Runnable() {
            @Override
            public void run() {
                syncAdd("1");
            }
        });
        Thread unSync = new Thread(new Runnable() {
            @Override
            public void run() {
                add("2");
            }
        });
        sync.start();
        unSync.start();
        syncAdd("3");
        Thread.sleep(1000);
        System.out.println(list);
    }
}
