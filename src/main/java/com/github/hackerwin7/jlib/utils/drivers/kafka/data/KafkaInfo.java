package com.github.hackerwin7.jlib.utils.drivers.kafka.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/16
 * Time: 2:01 PM
 * Desc: kafka machine information
 */
public class KafkaInfo {

    /*data*/
    private String brokers = null;
    private String zks = null;
    private String topic = null;

    private KafkaInfo(KafkaInfoBuilder builder) {
        brokers = builder.brokers;
        zks = builder.zks;
        topic = builder.topic;
    }

    public static KafkaInfoBuilder createBuilder() {
        return new KafkaInfoBuilder();
    }

    public static class KafkaInfoBuilder {
        private String brokers = null;
        private String zks = null;
        private String topic = null;

        private KafkaInfoBuilder() {

        }

        public KafkaInfoBuilder brokers(String brokers) {
            this.brokers = brokers;
            return this;
        }

        public KafkaInfoBuilder zks(String zks) {
            this.zks = zks;
            return this;
        }

        public KafkaInfoBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public KafkaInfo build() {
            return new KafkaInfo(this);
        }
    }

    public String getBrokers() {
        return brokers;
    }

    public String getZks() {
        return zks;
    }

    public String getTopic() {
        return topic;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("kafka info =>").append("\n");
        sb.append("---> brokers = " + brokers).append("\n");
        sb.append("---> zks = " + zks).append("\n");
        sb.append("---> topic = " + topic);
        return sb.toString();
    }
}
