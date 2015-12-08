package com.github.hackerwin7.jlib.utils.drivers.kafka.conf;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/08
 * Time: 2:30 PM
 * Desc: kafka client config
 */
public class KafkaConf {
    /*logger*/
    private static final Logger logger = Logger.getLogger(KafkaConf.class);

    /*data*/
    private Properties props = new Properties();

    /*constants*/
    /*  producer config*/
    public static final String PRODUCER_BROKER_LIST  = "metadata.broker.list";
    public static final String PRODUCER_ACKS = "request.required.acks";
    public static final String PRODUCER_REQUEST_TIMEOUT = "request.timeout.ms";
    public static final String PRODUCER_TYPE = "producer.type";
    public static final String PRODUCER_VALUE_SERIALIZER_CLASS = "serializer.class";
    public static final String PRODUCER_KEY_SERIALIZER_CLASS = "key.serializer.class";
    public static final String PRODUCER_PARTITION_CLASS = "partitioner.class";
    public static final String PRODUCER_COMPRESSION_CODEC = "compression.codec";

    /*  high level config*/
    public static final String HIGH_ZOOKEEPER_CONN = "zookeeper.connect";
    public static final String HIGH_GROUP_ID = "group.id";
    public static final String HIGH_ZOOKEEPER_SESSION_TIMEOUT = "zookeeper.session.timeout.ms";
    public static final String HIGH_ZOOKEEPER_SYNC_TIME = "zookeeper.sync.time.ms";
    public static final String HIGH_AUTO_COMMIT_INTERVAL = "auto.commit.interval.ms";
    public static final String HIGH_TOPIC = "high.level.topic";

    /*  simple config*/


    /**
     * default props
     */
    public KafkaConf() {
        props.put(PRODUCER_BROKER_LIST, "127.0.0.1:9092");
        props.put(PRODUCER_ACKS, "-1");
        props.put(PRODUCER_VALUE_SERIALIZER_CLASS, "kafka.serializer.DefaultEncoder");
        props.put(PRODUCER_KEY_SERIALIZER_CLASS, "kafka.serializer.StringEncoder");
        props.put(PRODUCER_PARTITION_CLASS, "kafka.producer.DefaultPartitioner");
    }

    /**
     * set customer kafka config
     * @param key
     * @param val
     */
    public void setProp(String key, String val) {
        props.put(key, val);
    }

    /**
     * get properties value
     * @param key
     * @return
     */
    public String getProp(String key) {
        return props.get(key).toString();
    }

    /**
     * return config string show
     * @return string value config
     */
    public String toString() {
        return props.toString();
    }

    /**
     * get config properties
     * @return props
     */
    public Properties getProps() {
        return props;
    }
}
