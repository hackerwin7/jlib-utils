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
    public static final String SIMPLE_BROKER_LIST = "simple.brokers";
    public static final String SIMPLE_PARTITIONS = "simple.partitions";
    public static final String SIMPLE_OFFSETS = "simple.offsets";
    public static final String SIMPLE_END_OFFSETS = "simple.end.offsets";
    public static final String SIMPLE_TOPIC = "simple.topic";

    /*  jdq producer config*/
    public static final String JDQ_ZK_INFO = "jdq.zk.info";
    public static final String JDQ_IS_SYNC = "jdq.is.sync";
    public static final String JDQ_COMPRESSION = "jdq.compression";
    public static final String JDQ_REPLICA_LEVEL = "jdq.replica.level";//all, no, one
    public static final String JDQ_IS_CONTROL_SPEED = "jdq.is.control.speed";

    /**
     * default props
     */
    public KafkaConf() {
        //default config for kafka config
        //producer config
        props.put(PRODUCER_BROKER_LIST, "127.0.0.1:9092");
        props.put(PRODUCER_ACKS, "-1");
        props.put(PRODUCER_VALUE_SERIALIZER_CLASS, "kafka.serializer.DefaultEncoder");
        props.put(PRODUCER_KEY_SERIALIZER_CLASS, "kafka.serializer.StringEncoder");
        props.put(PRODUCER_PARTITION_CLASS, "kafka.producer.DefaultPartitioner");
        //high config
        props.put(HIGH_ZOOKEEPER_CONN, "127.0.0.1:2181");
        props.put(HIGH_GROUP_ID, System.currentTimeMillis());
        props.put(HIGH_ZOOKEEPER_SESSION_TIMEOUT, "400");
        props.put(HIGH_ZOOKEEPER_SYNC_TIME, "200");
        props.put(HIGH_AUTO_COMMIT_INTERVAL, "1000");
        props.put(HIGH_TOPIC, "test");
        //simple config
        props.put(SIMPLE_BROKER_LIST, "127.0.0.1:9092");
        props.put(SIMPLE_PARTITIONS, "0");
        props.put(SIMPLE_OFFSETS, "0");
        props.put(SIMPLE_END_OFFSETS, Long.MAX_VALUE);
        props.put(SIMPLE_TOPIC, "test");
        //jdq producer
        props.put(JDQ_ZK_INFO, "127.0.0.1:2181/kafka");
        props.put(JDQ_IS_SYNC, "true");
        props.put(JDQ_COMPRESSION, "none");
        props.put(JDQ_REPLICA_LEVEL, "all");
        props.put(JDQ_IS_CONTROL_SPEED, "false");
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
