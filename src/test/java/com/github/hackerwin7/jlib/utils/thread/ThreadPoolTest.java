package com.github.hackerwin7.jlib.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/07/28
 * Time: 3:14 PM
 * Desc:
 * Tips:
 */
public class ThreadPoolTest {
    public static void main(String[] args) throws Exception {
        ThreadPoolTest tpt = new ThreadPoolTest();
        tpt.start();
    }

    public void start() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(int i = 1; i <= 10; i++) {
            executor.execute(new WorkerThread(i));
        }
        //executor.shutdown();
        executor.shutdownNow();
        while (!executor.isTerminated()) { // never true unless shutdown or shutdownNow see the javadoc
            Thread.sleep(1000);
        }
        System.out.println("end.");
    }

    class WorkerThread implements Runnable {

        private int sec = 0;

        public WorkerThread(int x) {
            sec = x;
        }

        @Override
        public void run() {
            try {
                System.out.println("sleeping " + sec + " secs");
                Thread.sleep(sec * 1000);
                System.out.println("slept " + sec + " secs");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
