package com.github.hackerwin7.jlib.utils.drivers.kafka.consumer;

import com.github.hackerwin7.jlib.utils.drivers.kafka.conf.KafkaConf;
import com.github.hackerwin7.jlib.utils.drivers.kafka.data.KafkaMsg;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.common.ErrorMapping;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/10
 * Time: 10:57 AM
 * Desc: kafka api for simple consumer to control offset
 */
public class KafkaSimpleConsumer {
    /*logger*/
    private static Logger logger = Logger.getLogger(KafkaSimpleConsumer.class);

    /*constants*/
    public static final int QUEUE_SIZE = 10000;
    public static final String CONF_SPLIT = ",";
    public static final String PORT_SPLIT = ":";
    public static final int CONSUME_TIME_OUT = 100000;
    public static final int CONSUME_BUFFER_SIZE = 64 * 1024;
    public static final int FETCH_SIZE = 1024 * 1024;
    public static final int ERR_COUNT_RECONN = 5;
    public static final long SLEEPING_TIME = 3000;

    /*data*/
    private String topic = null;
    private Map<String, Integer> brokers = new HashMap<>();//broker host -> broker port
    private Map<Integer, Long> offsets = new HashMap<>();//partition -> offset
    private Map<Integer, Long> endOffsets = new HashMap<>();
    private BlockingQueue<KafkaMsg> queue = new LinkedBlockingQueue<>(QUEUE_SIZE);

    /*driver*/
    private Map<Integer, SimpleConsumer> consumers = new HashMap<>();// consumer pool, topic partition -> simple consumer

    /**
     * constructor by kafka config
     * @param conf
     */
    public KafkaSimpleConsumer(KafkaConf conf) {
        String brokersStr = conf.getProp(KafkaConf.SIMPLE_BROKER_LIST);
        String partitionsStr = conf.getProp(KafkaConf.SIMPLE_PARTITIONS);
        String offsetsStr = conf.getProp(KafkaConf.SIMPLE_OFFSETS);
        String endOffsetsStr = conf.getProp(KafkaConf.SIMPLE_END_OFFSETS);
        String topicStr = conf.getProp(KafkaConf.SIMPLE_TOPIC);
        //topic
        topic = topicStr;
        //brokers
        String[] brokersArr = StringUtils.split(brokersStr, CONF_SPLIT);
        for(String broker : brokersArr) {
            String[] brokerArr = StringUtils.split(broker, PORT_SPLIT);
            String brokerSeed = brokerArr[0];
            Integer port = Integer.valueOf(brokerArr[1]);
            brokers.put(brokerSeed, port);
        }
        //partitions
        String[] partitionsArr = StringUtils.split(partitionsStr, CONF_SPLIT);
        //offsets
        String[] offsetsArr = StringUtils.split(offsetsStr, CONF_SPLIT);
        for(int i = 0; i <= partitionsArr.length - 1; i++) {
            Integer partition = Integer.valueOf(partitionsArr[i]);
            Long offset = Long.valueOf(offsetsArr[i]);
            offsets.put(partition, offset);
        }
        //endOffsets
        String[] endOffsetsArr = StringUtils.split(endOffsetsStr, CONF_SPLIT);
        for(int i = 0; i <= partitionsArr.length - 1; i++) {
            Integer partition = Integer.valueOf(partitionsArr[i]);
            Long endOffset = Long.MAX_VALUE;
            if(i <= endOffsetsArr.length - 1) {
                endOffset = Long.valueOf(endOffsetsArr[i]);
            }
            endOffsets.put(partition, endOffset);
        }
        //start consumer
        start();
    }

    /**
     * start the all consumer to running put to the queue
     */
    private void start() {

    }

    /**
     * start the thread to run the consumer
     */
    public class ConsumeThread implements Runnable {
        /*logger*/
        private Logger logger = Logger.getLogger(ConsumeThread.class);
        /*data*/
        private int partition = 0;
        private long offset = 0;
        private long endOffset = 0;
        private HashMap<String, Integer> brokers = null;//concurrent map get no put
        private AtomicBoolean running = new AtomicBoolean(true);
        /*driver*/
        private SimpleConsumer consumer = null;

        /**
         * constructor with broker and partition, offset
         * @param brokers
         * @param partition
         * @param beginOffset
         * @param endOffset
         */
        public ConsumeThread(HashMap<String, Integer> brokers, int partition, long beginOffset, long endOffset) {
            this.brokers = brokers;
            this.partition = partition;
            this.offset = beginOffset;
            this.endOffset = endOffset;
        }

        /**
         * run consumer to consume the kafka message
         */
        public void run() {
            PartitionMetadata metadata = findLeader();
            if(metadata == null) {
                logger.error("can not find metadata for topic = " + topic);
                return;
            }
            if(metadata.leader() == null) {
                logger.error("can not find leader for topic = " + topic);
                return;
            }
            String leader = metadata.leader().host();
            int port  = metadata.leader().port();
            String clientName = "simple_" + System.currentTimeMillis();
            consumer = new SimpleConsumer(leader, port, CONSUME_TIME_OUT, CONSUME_BUFFER_SIZE, clientName);
            //while status var
            long readOffset = offset;
            int numErr = 0;
            while (running.get()) {
                FetchRequest request = new FetchRequestBuilder()
                        .clientId(clientName)
                        .addFetch(topic, partition, readOffset, FETCH_SIZE)
                        .build();
                FetchResponse response = consumer.fetch(request);
                if(response.hasError()) {//error deal
                    numErr++;
                    short code = response.errorCode(topic, partition);
                    logger.error("receive error response from broker = " + leader + ", port = " + port + ", topic = " + topic + ", partition = " + partition + ", err code = " + code);
                    if(numErr >= ERR_COUNT_RECONN) { // exit consumer
                        logger.error("continuous receive " + numErr + " responses, exiting consumer thread......");
                        return;
                    } else if(code == ErrorMapping.OffsetOutOfRangeCode()) { // reset read offset
                        long minOffset = getMinOffset();
                        long maxOffset = getMaxOffset();
                        logger.error("encounter out off range fetch error.");
                        logger.error("min offset = " + minOffset + ", max offset = " + maxOffset + ", request offset = " + readOffset);
                        if(readOffset < minOffset) {
                            readOffset = minOffset;
                        } else if(readOffset > maxOffset) {
                            readOffset = maxOffset;
                        } else {
                            /*no op*/
                        }
                        logger.error("reset the request offset to " + readOffset + ", in partition = " + partition + ", of topic = " + topic);
                    } else { // find new leader and rebuild consumer
                        logger.error("leader broker maybe switched , finding ......");
                        logger.error("old leader = " + leader + ", port = " + port);
                        consumer.close();
                        metadata = findLeader();
                        if(metadata == null) {
                            logger.error("can not find metadata for topic = " + topic);
                            logger.error("exiting consumer thread...");
                            return;
                        }
                        if(metadata.leader() == null) {
                            logger.error("can not find leader for topic = " + topic);
                            logger.error("exiting consumer thread...");
                            return;
                        }
                        leader = metadata.leader().host();
                        port = metadata.leader().port();
                        consumer = new SimpleConsumer(leader, port, CONSUME_TIME_OUT, CONSUME_BUFFER_SIZE, clientName);
                        logger.error("new leader = " + leader + ", port = " + port);
                    }
                } else {// no error
                    numErr = 0;//clear the continuous err number
                    for(MessageAndOffset messageAndOffset : response.messageSet(topic, partition)) {
                        long curOffset = messageAndOffset.offset();
                        if(curOffset < readOffset) {
                            logger.error("found an old offset = " + curOffset + ", expect read offset = " + readOffset);
                        } else {//put message to offset
                            //header
                            long nextOffset = messageAndOffset.nextOffset();
                            long currentOffset = curOffset;
                            byte[] val  = null;
                            String key = null;
                            String topicMsg = null;
                            int partitionMsg = 0;
                            //deal value
                            ByteBuffer valBuffer = messageAndOffset.message().payload();
                            byte[] valBytes = new byte[valBuffer.limit()];
                            valBuffer.get(valBytes);
                            val = valBytes;
                            //deal key
                            if(messageAndOffset.message().hasKey()) {
                                ByteBuffer keyBuffer = messageAndOffset.message().key();
                                byte[] keyBytes = new byte[keyBuffer.limit()];
                                keyBuffer.get(keyBytes);
                                key = new String(keyBytes);
                            }
                            //deal topic
                            topicMsg = topic;
                            //deal partition
                            partitionMsg = partition;
                            //make kafka message
                            KafkaMsg msg = KafkaMsg.createBuilder()
                                    .offset(currentOffset)
                                    .nextOffset(nextOffset)
                                    .val(val)
                                    .key(key)
                                    .topic(topicMsg)
                                    .partition(partitionMsg)
                                    .build();
                            //put into queue
                            while (true) {
                                try {
                                    queue.put(msg);
                                    break;
                                } catch (InterruptedException e) {
                                    logger.error(e.getMessage(), e);
                                    try {
                                        Thread.sleep(SLEEPING_TIME);
                                    } catch (InterruptedException ee) {
                                        logger.error(ee.getMessage(), ee);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * find specified topic's leader broker
         * @return leader
         */
        private PartitionMetadata findLeader() {
            PartitionMetadata metadata = null;
            loop:
            for(Map.Entry<String, Integer> brokerPort : brokers.entrySet()) {
                String seed = brokerPort.getKey();
                int port = brokerPort.getValue();
                String clientName = "find_leader" + System.currentTimeMillis();
                SimpleConsumer consumer = null;
                try {
                    consumer = new SimpleConsumer(seed, port, CONSUME_TIME_OUT, CONSUME_BUFFER_SIZE, clientName);
                    List<String> topics = Collections.singletonList(topic);
                    TopicMetadataRequest request = new TopicMetadataRequest(topics);
                    TopicMetadataResponse response = consumer.send(request);
                    List<TopicMetadata> metadatas = response.topicsMetadata();
                    for(TopicMetadata item : metadatas) {
                        for(PartitionMetadata part : item.partitionsMetadata()) {
                            if(part.partitionId() == partition) {
                                metadata = part;
                                break loop;
                            }
                        }
                    }
                } catch (Throwable e) {
                    logger.error("error communicating with broker = [" + seed + "] to find leader for topic = [" + topic + "], partition = [" + partition + "]");
                } finally {
                    if(consumer != null)
                        consumer.close();
                }
            }
            return metadata;
        }
    }
}
