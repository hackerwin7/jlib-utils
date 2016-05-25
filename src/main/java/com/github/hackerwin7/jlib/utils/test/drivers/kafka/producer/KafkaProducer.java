package com.github.hackerwin7.jlib.utils.test.drivers.kafka.producer;

import com.github.hackerwin7.jlib.utils.test.drivers.kafka.conf.KafkaConf;
import com.github.hackerwin7.jlib.utils.test.drivers.kafka.data.KafkaMsg;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/08
 * Time: 11:45 AM
 * Desc: kafka producer client
 */
public class KafkaProducer {
    /*logger*/
    private static Logger logger = Logger.getLogger(KafkaProducer.class);

    /*driver*/
    private Producer<String, byte[]> producer = null;

    /**
     * constructor kafka producer client by kafka conf
     * @param conf
     */
    public KafkaProducer(KafkaConf conf) {
        ProducerConfig config = new ProducerConfig(conf.getProps());
        producer = new Producer<String, byte[]>(config);
        logger.info("loaded kafka producer config = " + conf);
    }

    /**
     * close producer client
     */
    public void close() {
        if(producer != null)
            producer.close();
    }

    /**
     * sendKafkaMsg single
     * @param msg
     */
    public void sendKafkaMsg(KafkaMsg msg) {
        if(msg != null)
            producer.send(toKeyedMessage(msg));
    }

    /**
     * sendKafkaMsg batch
     * @param msgs
     */
    public void sendKafkaMsg(List<KafkaMsg> msgs) {
        if(msgs.size() > 0)
            producer.send(toKeyedMessage(msgs));
    }

    public void send(KeyedMessage<String, byte[]> msg) {
        if(msg != null)
            producer.send(msg);
    }

    public void send(List<KeyedMessage<String, byte[]>> msgs) {
        if(msgs.size() > 0)
            producer.send(msgs);
    }

    /**
     * convert to keyedmessage
     * @param msg
     * @return
     */
    public static KeyedMessage<String, byte[]> toKeyedMessage(KafkaMsg msg) {
        return new KeyedMessage<String, byte[]>(msg.getTopic(), msg.getKey(), msg.getVal());
    }

    /**
     * convert to batch keyedmessages
     * @param msgs
     * @return
     */
    public static List<KeyedMessage<String, byte[]>> toKeyedMessage(List<KafkaMsg> msgs) {
        List<KeyedMessage<String, byte[]>> keyedMessages = new ArrayList<>();
        for(KafkaMsg msg : msgs) {
            keyedMessages.add(new KeyedMessage<String, byte[]>(msg.getTopic(), msg.getKey(), msg.getVal()));
        }
        return keyedMessages;
    }
}
