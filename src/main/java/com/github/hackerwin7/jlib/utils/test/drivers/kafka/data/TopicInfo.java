package com.github.hackerwin7.jlib.utils.test.drivers.kafka.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/16
 * Time: 2:01 PM
 * Desc: kafka machine information
 */
public class TopicInfo {

    /*data*/
    private String brokers = null;
    private String zks = null;
    private String topic = null;
    private int partitionNum = 0;
    private Map<Integer, String> partitionBroker = new HashMap<>();
    private Map<Integer, Long> beginOffsets = new HashMap<>();
    private Map<Integer, Long> endOffsets = new HashMap<>();

    private TopicInfo(KafkaInfoBuilder builder) {
        brokers = builder.brokers;
        zks = builder.zks;
        topic = builder.topic;
        partitionNum = builder.partitionNum;
    }

    public static KafkaInfoBuilder createBuilder() {
        return new KafkaInfoBuilder();
    }

    public static class KafkaInfoBuilder {
        private String brokers = null;
        private String zks = null;
        private String topic = null;
        private int partitionNum = 0;

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

        public KafkaInfoBuilder partitionNum (int partitionNum) {
            this.partitionNum = partitionNum;
            return this;
        }

        public TopicInfo build() {
            return new TopicInfo(this);
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

    public int getPartitionNum() {
        return partitionNum;
    }

    public void putOffset(int partition, long offset) {
        beginOffsets.put(partition, offset);
    }

    public void putEndOffset(int partition, long endOffset) {
        endOffsets.put(partition, endOffset);
    }

    public long getoffset(int partition) {
        return beginOffsets.get(partition);
    }

    public long getEndOffsets(int partition) {
        return endOffsets.get(partition);
    }

    public Map<Integer, Long> getBeginOffsetsMap() {
        return beginOffsets;
    }

    public Map<Integer, Long> getEndOffsetsMap() {
        return endOffsets;
    }

    public void putPartitionBroker(int partition, String brokers) {
        partitionBroker.put(partition, brokers);
    }

    public String getBrokers(int partition) {
        return partitionBroker.get(partition);
    }

    public Map<Integer, String> getBrokerMap() {
        return partitionBroker;
    }

    public void putBeginoffsetsAll(Map<Integer, Long> map) {
        beginOffsets.putAll(map);
    }

    public void putEndOffsetsAll(Map<Integer, Long> map) {
        endOffsets.putAll(map);
    }

    public void putBrokersAll(Map<Integer, String> map) {
        partitionBroker.putAll(map);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("kafka info =>").append("\n");
        sb.append("---> brokers = " + brokers).append("\n");
        sb.append("---> zks = " + zks).append("\n");
        sb.append("---> topic = " + topic).append("\n");
        sb.append("---> partition offset = " + beginOffsets.toString()).append("\n");
        sb.append("---> partition end offset = " + endOffsets.toString());
        return sb.toString();
    }
}
