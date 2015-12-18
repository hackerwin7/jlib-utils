package com.github.hackerwin7.jlib.utils.drivers.kafka.utils;

import com.github.hackerwin7.jlib.utils.drivers.kafka.consumer.KafkaSimpleConsumer;
import com.github.hackerwin7.jlib.utils.drivers.kafka.data.TopicInfo;
import com.github.hackerwin7.jlib.utils.drivers.zk.ZkClient;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/18
 * Time: 11:19 AM
 * Desc: obtain kafka information using zk, independent kafka utils
 */
public class KafkaUtils {
    /*logger*/
    private static Logger logger = Logger.getLogger(KafkaUtils.class);

    /*data*/
    private String zkconn = null;
    private String zkroot = "";
    private Map<String, String> idsBrokers = new HashMap<>();
    private List<String> brokerSeeds = new ArrayList<>();
    private List<String> replicaBrokers = new ArrayList<>();

    /*driver*/
    private ZkClient zk = null;
    private Map<Integer, SimpleConsumer> querys = new HashMap<>();//consumer pool

    /*constants*/
    public static final String ZK_TOPICS_PATH = "/brokers/topics";
    public static final String ZK_IDS_PATH = "/brokers/ids";
    public static final long SLEEPING_TIME = 3000;

    /**
     * build zk client and load global broker id mapping
     * @param zkconnStr
     */
    public KafkaUtils(String zkconnStr) throws Exception {
        //divide into zk conn and zk root
        String[] nodes = StringUtils.split(zkconnStr, ",");
        if(nodes.length >= 2) { // specific zk root
            for(int i = 0; i <= nodes.length - 1; i++) {
                if(i == 0)
                    zkconn = nodes[0];
                else
                    zkroot += "/" + nodes[i];
            }
        } else if(nodes.length == 1) { // zk root is "/"
            zkconn = nodes[0];
            zkroot = "/";
        } else { // error format
            throw new Exception("error format zk connection string = " + zkconnStr);
        }
        //build zk client and load meta data
        zk = new ZkClient(zkconn);
        String idsPath = zkroot + ZK_IDS_PATH;
        List<String> ids = zk.getChildren(idsPath);
        for(String brokerId : ids) {
            String val = zk.get(idsPath + "/" + brokerId);
            JSONObject jval = JSONObject.fromObject(val);
            String brokers = jval.getString("host") + ":" + jval.getString("port");
            idsBrokers.put(brokerId, brokers);
            brokerSeeds.add(brokers);
        }
    }

    /**
     * get topic information by using topic and zkconn
     * @param topic
     * @return topic info
     */
    public TopicInfo findTopicInfo(String topic) throws Exception {
        //zk path
        String topicPath = zkroot + ZK_TOPICS_PATH + "/" + topic;
        String partitionPath = topicPath + "/partitions";
        //get zk data
        String topicInfoStr = zk.get(topicPath);
        List<String> partitionsList = zk.getChildren(partitionPath);
        //make partitions
        int partitionNum = partitionsList.size();// 0 ~ size - 1
        //using partition to build    broker info, consumer interface, offset query
        Map<Integer, String> partitionBrokers = new HashMap<>();
        Map<Integer, Long> beginOffsets = new HashMap<>();
        Map<Integer, Long> endOffsets = new HashMap<>();
        for(String partStr : partitionsList) {
            //get partition
            int partition = Integer.valueOf(partStr);
            //find broker info
            String brokerStr = findPartitionBrokerStr(topicInfoStr, partStr);
            partitionBrokers.put(partition, brokerStr);
            //build consumer
            SimpleConsumer consumer = findConsumer(brokerSeeds, topic, partition);
            querys.put(partition, consumer);
            //find min/max offset
            long minOffset = getMinOffset(consumer, topic, partition);
            long maxOffset = getMaxOffset(consumer, topic, partition);
            beginOffsets.put(partition, minOffset);
            endOffsets.put(partition, maxOffset);
        }
        //build topic info
        TopicInfo info = TopicInfo.createBuilder()
                .topic(topic)
                .zks(zkconn + zkroot)
                .brokers(idsBrokers.toString())//whole broker
                .build();
        info.putBrokersAll(partitionBrokers);
        info.putBeginoffsetsAll(beginOffsets);
        info.putEndOffsetsAll(endOffsets);
        return info;
    }

    private String findPartitionBrokerStr(String jsonStr, String partStr) {
        //build brokers info
        List<String> brokers = new ArrayList<>();
        //  parse json
        JSONObject jtInfo = JSONObject.fromObject(jsonStr);
        JSONArray jbrokers = jtInfo.getJSONObject("partitions").getJSONArray(partStr);
        for(int i = 0; i <= jbrokers.size() - 1; i++) {
            int bid = jbrokers.getInt(i);
            brokers.add(idsBrokers.get(String.valueOf(bid)));
        }
        return StringUtils.join(brokers, ",");
    }

    /**
     * make kafka consumer interface
     * @param brokers
     * @param topic
     * @param partition
     * @return simple consumer
     */
    private SimpleConsumer findConsumer(List<String> brokers, String topic, int partition) throws Exception {
        PartitionMetadata metadata = findLeader(brokers, topic, partition);
        if(metadata == null) {
            throw new Exception("find leader error......");
        }
        if(metadata.leader().host() == null) {
            throw new Exception("find leader host error");
        }
        String leaderHost = metadata.leader().host();
        int port = metadata.leader().port();
        return new SimpleConsumer(leaderHost, port, KafkaSimpleConsumer.CONSUME_TIME_OUT, KafkaSimpleConsumer.CONSUME_BUFFER_SIZE, "fff" + System.currentTimeMillis());
    }


    /**
     * find specified topic's leader broker
     * @return leader
     */
    private PartitionMetadata findLeader(List<String> brokers, String topic, int partition) {
        PartitionMetadata metadata = null;
        loop:
        for(String brokerPort : brokers) {
            String[] bpArr = StringUtils.split(brokerPort, ":");
            String seed = bpArr[0];
            int port = Integer.valueOf(bpArr[1]);
            String clientName = "find_leader" + System.currentTimeMillis();
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(seed, port, KafkaSimpleConsumer.CONSUME_TIME_OUT, KafkaSimpleConsumer.CONSUME_BUFFER_SIZE, clientName);
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
        if(replicaBrokers != null) {
            replicaBrokers.clear();
            for(kafka.cluster.Broker replica : metadata.replicas()) {
                replicaBrokers.add(replica.host() + ":" + replica.port());//add replica broker to save
            }
        }
        return metadata;
    }

    public static long getMinOffset(SimpleConsumer consumer, String topic, int partition) throws Exception {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfoMap = new HashMap<>();
        requestInfoMap.put(topicAndPartition, new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.EarliestTime(), 1));
        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfoMap, kafka.api.OffsetRequest.CurrentVersion(), "find offset " + System.currentTimeMillis());
        OffsetResponse response = consumer.getOffsetsBefore(request);
        if(response.hasError()) {
            throw new Exception("error fetching data offset , reason = " + response.errorCode(topic, partition));
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }

    public static long getMaxOffset(SimpleConsumer consumer, String topic, int partition) throws Exception {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfoMap = new HashMap<>();
        requestInfoMap.put(topicAndPartition, new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1));
        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfoMap, kafka.api.OffsetRequest.CurrentVersion(), "find offset " + System.currentTimeMillis());
        OffsetResponse response = consumer.getOffsetsBefore(request);
        if(response.hasError()) {
            throw new Exception("error fetching data offset , reason = " + response.errorCode(topic, partition));
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }


}
