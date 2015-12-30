package com.github.hackerwin7.jlib.utils.frameworks.magpie;

import org.apache.log4j.Logger;
import org.apache.log4j.or.ThreadGroupRenderer;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/28
 * Time: 4:24 PM
 * Desc: topology main process for topology
 */
public class Topology {

    /*logger*/
    private Logger logger = Logger.getLogger(Topology.class);

    /*driver*/
    private MagpieExecutor executor = null;

    /*constants*/
    public static final long SLEEP_INTERVAL = 5 * 1000;

    /**
     * construct
     * @param executor
     */
    public Topology(MagpieExecutor executor) {
        this.executor = executor;
    }

    /**
     * main process
     * while run executor's run
     */
    public void run() {
        while (true) {
            try {
                executor.prepare();
                Thread.sleep(SLEEP_INTERVAL);
                while (true) {
                    executor.run();
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    Thread.sleep(SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
