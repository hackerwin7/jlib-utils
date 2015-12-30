package com.github.hackerwin7.jlib.utils.frameworks.magpie;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/28
 * Time: 4:25 PM
 * Desc: prepare run reload close
 */
public interface MagpieExecutor {

    /**
     * prepare
     * load conf, start thread etc...
     * @throws Exception
     */
    public void prepare() throws Exception;

    /**
     * main process
     * while run
     * @throws Exception
     */
    public void run() throws Exception;

    /**
     * close
     * @throws Exception
     */
    public void close() throws Exception;

    /**
     * reload
     * @throws Exception
     */
    public void reload() throws Exception;
}
