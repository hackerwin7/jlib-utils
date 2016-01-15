package com.github.hackerwin7.jlib.utils.drivers.queue.blocking;

import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/11/19
 * Time: 4:33 PM
 * Desc: wrapper on jdk bloking queue, use LinkedBlockingQueue inner
 */
public class BlockingDataQueue<E> {
    /*logger*/
    private static final Logger logger = Logger.getLogger(BlockingDataQueue.class);

    /*data*/
    private BlockingQueue<E> queue = null;

    /**
     * make queue by size
     * @param size
     */
    public BlockingDataQueue(int size) {
        queue = new LinkedBlockingQueue<E>(size);
    }

    /**
     * put in this data, data is not null
     * @param e
     * @throws Exception
     */
    public void put(E e) throws Exception {
        if(e != null)
            queue.put(e);
    }

    /**
     * consume a data5959
     * @return E data
     * @throws Exception
     */
    public E take() throws Exception {
        return queue.take();
    }

    /**
     * the size of queue
     * @return queue length
     */
    public int size() {
        return queue.size();
    }

    /**
     * is empty or not
     * @return boolean empty
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * clear the queue
     */
    public void clear() {
        queue.clear();
    }
}
