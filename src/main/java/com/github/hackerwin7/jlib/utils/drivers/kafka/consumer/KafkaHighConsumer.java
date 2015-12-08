package com.github.hackerwin7.jlib.utils.drivers.kafka.consumer;

import com.github.hackerwin7.jlib.utils.drivers.kafka.conf.KafkaConf;
import com.github.hackerwin7.jlib.utils.drivers.kafka.data.KafkaMsg;
import kafka.Kafka;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/08
 * Time: 4:14 PM
 * Desc: kafka high level consumer client
 */
public class KafkaHighConsumer {
    /*logger*/
    private static Logger logger = Logger.getLogger(KafkaHighConsumer.class);

    /*constants*/
    public static final int QUEUE_SIZE = 10000;
    public static final long SLEEPING_INTERVAL = 3000;
    public static final int THREAD_COUNT = 5;

    /*driver*/
    private ConsumerConnector consumer = null;

    /*thread pool*/
    private ExecutorService executor = null;

    /*topic*/
    private String topic = null;

    /*queue, consumer take queue*/
    private BlockingQueue<KafkaMsg> queue = new LinkedBlockingQueue<>(QUEUE_SIZE);

    /**
     * constructor with kafka conf
     * @param conf
     */
    public KafkaHighConsumer(KafkaConf conf) {
        ConsumerConfig config = new ConsumerConfig(conf.getProps());
        consumer = Consumer.createJavaConsumerConnector(config);
        topic = conf.getProp(KafkaConf.HIGH_TOPIC);
    }

    /**
     * consumer thread running consume
     */
    public class ConsumerTh implements Runnable {
        private KafkaStream<byte[], byte[]> stream = null;
        private int threadNum = 0;

        public ConsumerTh(KafkaStream<byte[], byte[]> stream, int threadNum) {
            this.stream = stream;
            this.threadNum = threadNum;
        }

        public void run() {
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            while (it.hasNext()) {
                try {
                    KafkaMsg msg = KafkaMsg.createBuilder()
                            .key(new String(it.next().key()))
                            .val(it.next().message())
                            .offset(it.next().offset())
                            .partition(it.next().partition())
                            .topic(it.next().topic())
                            .build();
                    while (true) {//retry put
                        try {
                            queue.put(msg);
                            break;
                        } catch (InterruptedException e) {
                            logger.error(e.getMessage(), e);
                            try {
                                Thread.sleep(SLEEPING_INTERVAL);
                            } catch (InterruptedException ee) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * start the consumer client, start the thread pool to consume topic
     */
    public void start() {
        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put(topic, THREAD_COUNT);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        executor = Executors.newFixedThreadPool(THREAD_COUNT);
        int threadNum = 0;
        for(final KafkaStream<byte[], byte[]> stream : streams) {
            executor.submit(new ConsumerTh(stream, threadNum));
            threadNum++;
        }
    }

    public void stop() {
        if(executor != null)
            executor.shutdownNow();
        try {
            Thread.sleep(SLEEPING_INTERVAL);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        if(consumer != null)
            consumer.shutdown();
    }

    /**
     * consume a msg
     * @return
     */
    public KafkaMsg consume() {
        while (true) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
                try {
                    Thread.sleep(SLEEPING_INTERVAL);
                } catch (InterruptedException ee) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * batch consume kafka
     * @param num
     * @return
     */
    public List<KafkaMsg> consume(int num, long timeout) {
        List<KafkaMsg> msgs = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        while (true) {
            while (true) {
                try {
                    msgs.add(queue.take());
                    break;
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                    try {
                        Thread.sleep(SLEEPING_INTERVAL);
                    } catch (InterruptedException ee) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            if(msgs.size() >= num) {
                break;
            }
            if(System.currentTimeMillis() - startTime >= timeout) {
                break;
            }
        }
        return msgs;
    }
}
