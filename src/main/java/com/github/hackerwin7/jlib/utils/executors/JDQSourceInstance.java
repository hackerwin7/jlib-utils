package com.github.hackerwin7.jlib.utils.executors;

import com.jd.bdp.jdq.consumer.JDQSimpleConsumer;
import com.jd.bdp.jdq.consumer.simple.JDQSimpleMessage;
import com.jd.bdp.jdq.consumer.simple.OutOfRangeHandler;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by fff on 11/13/15.
 */
public class JDQSourceInstance {
    private static Logger logger = Logger.getLogger(JDQSourceInstance.class);

    public static void main(String[] args) throws Exception {
        String zkInfo = args[0];
        String brokerList = args[1];
        String topicInfo = args[2];
        int partitionInfo = 0;
        long offsetInfo = -1;
        JDQSimpleConsumer consumer = null;

        logger.info("config -> zk = " + zkInfo);
        logger.info("config -> brokers = " + brokerList);
        logger.info("config -> topic = " + topicInfo);

        while (true) {
            try {
                consumer = new JDQSimpleConsumer(zkInfo, brokerList, topicInfo);

                long minOffset = consumer.findValidityMinOffset(partitionInfo);
                long maxOffset = consumer.findValidityMaxOffset(partitionInfo);
                if (offsetInfo < minOffset || offsetInfo > maxOffset) {
                    logger.info("config offset = " + offsetInfo + ", max offset = " + maxOffset + ", min offset = " + minOffset);
                    offsetInfo = maxOffset;
                }

                while (true) {
                    List<JDQSimpleMessage> messages = consumer.consumeMessage(partitionInfo, offsetInfo, new OutOfRangeHandler() {
                        @Override
                        public long handleOffsetOutOfRange(long l, long l1, long l2) {
                            logger.error("offset in running is out of range (min cur max) : " + l + ", " + l2 + ", " + l1);
                            return l2;
                        }
                    });

                    if (messages.size() > 0) {
                        for (JDQSimpleMessage msg : messages) {
                            String dataContent = new String(msg.getData(), "UTF-8");
                            long dataOffset = msg.getOffset();
                            logger.info("----------------> consume offset = " + dataOffset);
                            logger.info("----------------> consume data = " + dataContent);
                        }
                        offsetInfo = messages.get(messages.size() - 1).getNextOffset();
                    } else {
                        logger.info("consume empty, sleeping ......");
                        Thread.sleep(3000);
                    }
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                logger.info("refreshing metadata ......");
            } finally {
                if(consumer != null) {
                    consumer.close();
                    consumer = null;
                }
                Thread.sleep(5000);
            }
        }
    }
}
