package com.github.hackerwin7.jlib.utils.jdk.concurrent.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/11/24
 * Time: 5:28 PM
 * Desc:
 */
public class FutureTest {
    public static void main(String[] args) throws Exception {
        FutureTest ft = new FutureTest();
        ft.proc();
    }


    public void proc() throws Exception {
        List<Future<Boolean>> futures = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Task task1 = new Task("t1");
        Task task2 = new Task("t2");
        futures.add(pool.submit(task1));
        futures.add(pool.submit(task2));
        boolean isok = true;
        for(Future<Boolean> future : futures) {
            isok &= future.get();
        }
        System.out.println(isok);
    }

    class Task implements Callable<Boolean> {
        private String name;

        public Task(String _name) {
            name = _name;
        }

        public Boolean call() throws Exception {
            System.out.println("name = " + name + " is sleeping......");
            Thread.sleep(10000);
            return false;
        }
    }
}
