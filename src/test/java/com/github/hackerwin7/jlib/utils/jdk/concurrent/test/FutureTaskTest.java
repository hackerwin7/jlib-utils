package com.github.hackerwin7.jlib.utils.jdk.concurrent.test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/17
 * Time: 6:36 PM
 * Desc:
 */
public class FutureTaskTest {
    public static void main(String[] args) throws Throwable {
        FutureTaskTest ft = new FutureTaskTest();
        ft.start();
    }

    private void start() throws Throwable {
        ThreadRun run = new ThreadRun();
        ExecutorService executors = Executors.newFixedThreadPool(5);
        Future<Boolean> fb = executors.submit(run);
        System.out.println(fb.get());
        System.out.println(run.call());
    }

    class ThreadRun implements Callable<Boolean> {
        public Boolean call() throws Exception {
            System.out.println("start running......");
            Thread.sleep(5000);
            throw new Exception("throw exception ......");
        }
    }
}
