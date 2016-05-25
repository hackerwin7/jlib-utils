package com.github.hackerwin7.jlib.utils.test.drivers.kafka.data;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/08
 * Time: 3:06 PM
 * Desc:
 */
public class KafkaMsg {
    private String key = null;
    private byte[] val = null;
    private long offset = 0;
    private long nextOffset = 0;
    private String topic = null;
    private int parititon = 0;

    /**
     * singleton
     */
    private KafkaMsg(KafkaMsgBuilder builder) {
        this.key = builder.key;
        this.val = builder.val;
        this.offset = builder.offset;
        this.nextOffset = builder.nextOffset;
        this.topic = builder.topic;
        this.parititon = builder.partition;
    }

    /**
     * builder class
     */
    public static class KafkaMsgBuilder {
        private String key = null;
        private byte[] val = null;
        private long offset = 0;
        private long nextOffset = 0;
        private String topic = null;
        private int partition = 0;

        private KafkaMsgBuilder() {

        }

        public KafkaMsgBuilder key(String key) {
            this.key = key;
            return this;
        }

        public KafkaMsgBuilder val(byte[] val) {
            this.val = val;
            return this;
        }

        public KafkaMsgBuilder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public KafkaMsgBuilder nextOffset(long nextOffset) {
            this.nextOffset = nextOffset;
            return this;
        }

        public KafkaMsgBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public KafkaMsgBuilder partition(int partition) {
            this.partition = partition;
            return this;
        }

        public KafkaMsg build() {
            return new KafkaMsg(this);
        }
    }

    /**
     * create builder
     * @return builder
     */
    public static KafkaMsgBuilder createBuilder() {
        return new KafkaMsgBuilder();
    }

    public String getKey() {
        return key;
    }

    public byte[] getVal() {
        return val;
    }

    public long getOffset() {
        return offset;
    }

    public long getNextOffset() {
        return nextOffset;
    }

    public String getTopic() {
        return topic;
    }

    public int getParititon() {
        return parititon;
    }
}
