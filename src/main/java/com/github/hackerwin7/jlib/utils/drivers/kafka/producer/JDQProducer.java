package com.github.hackerwin7.jlib.utils.drivers.kafka.producer;

import com.github.hackerwin7.jlib.utils.drivers.kafka.conf.KafkaConf;
import com.github.hackerwin7.jlib.utils.drivers.kafka.data.KafkaMsg;
import com.jd.bdp.jdq.config.Ack;
import com.jd.bdp.jdq.config.Compression;
import com.jd.bdp.jdq.config.Send;
import com.jd.bdp.jdq.producer.sysuser.JDQMessage;
import com.jd.bdp.jdq.producer.sysuser.JDQProducerClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2015/12/16
 * Time: 12:35 PM
 * Desc: jdq producer for kafka producer client
 */
public class JDQProducer {
    /*logger*/
    private static Logger logger = Logger.getLogger(JDQProducer.class);

    /*driver*/
    private JDQProducerClient<String, byte[]> producer = null;

    /*constants*/
    public static final String CONFIG_SYNC = "sync";
    public static final String CONFIG_COMPRESSION_NONE = "none";
    public static final String CONFIG_COMPRESSION_SNAPPY = "snappy";
    public static final String CONFIG_COMPRESSION_GZIP = "gzip";
    public static final String CONFIG_REPLICA_LEVEL_ALL = "all";
    public static final String CONFIG_REPLICA_LEVEL_NO = "no";
    public static final String CONFIG_REPLICA_LEVEL_ONE = "one";

    /**
     * constructor using kafka config
     * @param conf
     */
    public JDQProducer(KafkaConf conf) throws Exception {
        //load conf
        String zkinfo = conf.getProp(KafkaConf.JDQ_ZK_INFO);
        String isSync = conf.getProp(KafkaConf.JDQ_IS_SYNC);
        String compression = conf.getProp(KafkaConf.JDQ_COMPRESSION);
        String replocaLevel = conf.getProp(KafkaConf.JDQ_REPLICA_LEVEL);
        String isControlSpeed = conf.getProp(KafkaConf.JDQ_IS_CONTROL_SPEED);
        //build producer
        producer = new JDQProducerClient<String, byte[]>(zkinfo);
        if(StringUtils.equalsIgnoreCase(isSync, CONFIG_SYNC))
            producer.setAsync(Send.Sync);
        else
            producer.setAsync(Send.Async);
        switch (compression.toLowerCase()) {
            case CONFIG_COMPRESSION_SNAPPY:
                producer.setCompression(Compression.SNAPPY);
                break;
            case CONFIG_COMPRESSION_GZIP:
                producer.setCompression(Compression.GZIP);
                break;
            case CONFIG_COMPRESSION_NONE:
                producer.setCompression(Compression.NONE);
            default:
                producer.setCompression(Compression.valueOf(compression));
        }
        switch (replocaLevel.toLowerCase()) {
            case CONFIG_REPLICA_LEVEL_ALL:
                producer.setReplicaLevel(Ack.WriteAllAck);
                break;
            case CONFIG_REPLICA_LEVEL_ONE:
                producer.setReplicaLevel(Ack.WriteOneAck);
                break;
            case CONFIG_REPLICA_LEVEL_NO:
                producer.setReplicaLevel(Ack.NoAck);
                break;
            default:
                producer.setReplicaLevel(Ack.WriteAllAck);
        }
        if(StringUtils.equalsIgnoreCase(isControlSpeed, "true"))
            producer.isControlSpeed(true);
        else
            producer.isControlSpeed(false);
    }


    /**
     * send kafka msg
     * @param msg
     * @throws Exception
     */
    public void send(KafkaMsg msg) throws Exception {
        if(msg != null)
            producer.send(toJDQMsg(msg));
    }

    /**
     * batch send
     * @param msgs
     * @throws Exception
     */
    public void send(List<KafkaMsg> msgs) throws Exception {
        if(msgs.size() > 0)
            producer.send(toJDQMsgs(msgs));
    }

    public void sendJDQMsg(JDQMessage<String, byte[]> msg) throws Exception {
        if(msg != null)
            producer.send(msg);
    }

    public void sendJDQMsg(List<JDQMessage<String, byte[]>> msgs) throws Exception {
        if(msgs.size() > 0)
            producer.send(msgs);
    }

    /**
     * convert kafka msg to jdq msg
     * @param msg
     * @return jdq msg
     */
    public static JDQMessage<String, byte[]> toJDQMsg(KafkaMsg msg) {
        return new JDQMessage<>(msg.getTopic(), msg.getKey(), msg.getVal());
    }

    /**
     * convert batch
     * @param msgs
     * @return jdq msgs
     */
    public static List<JDQMessage<String, byte[]>> toJDQMsgs(List<KafkaMsg> msgs) {
        List<JDQMessage<String, byte[]>> jmsgs = new LinkedList<>();
        for(KafkaMsg msg : msgs) {
            jmsgs.add(toJDQMsg(msg));
        }
        return jmsgs;
    }
}
