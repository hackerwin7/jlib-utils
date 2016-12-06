package com.github.hackerwin7.jlib.utils.thread;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/12/06
 * Time: 2:04 PM
 * Desc:
 * Tips:
 */
public class SchedulerTest {

    private static final Logger LOG = Logger.getLogger(SchedulerTest.class);

    public static void main(String[] args) {
        SchedulerTest st = new SchedulerTest();
        st.scheduledExecutorServiceTest();

        while (true) {
            CommonUtils.delay(3000);
            LOG.info("main running ......");
        }
    }

    public void scheduledExecutorServiceTest() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                /* blocking method */

                LOG.info("come into blocking method ......");

                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage(), e);
                    }

                    LOG.info("blocking ......");
                }
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                LOG.info("come into non-blocking method ......");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                }

                LOG.info("non-blocking ...");
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }
}
