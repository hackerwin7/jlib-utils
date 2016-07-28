package com.github.hackerwin7.jlib.utils.thrift.impl;

import com.github.hackerwin7.jlib.utils.commons.CommonUtils;
import com.github.hackerwin7.jlib.utils.thrift.gen.trans.TFileChunk;
import com.github.hackerwin7.jlib.utils.thrift.gen.trans.TFileInfo;
import com.github.hackerwin7.jlib.utils.thrift.gen.trans.TFileService;
import com.github.hackerwin7.jlib.utils.thrift.gen.trans.transConstants;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/06/02
 * Time: 5:17 PM
 * Desc: implement with random read/write
 * Tips:
 */
public class TFileServiceHandler implements TFileService.Iface {

    /* logger */
    private static final Logger LOG = Logger.getLogger(TFileServiceHandler.class);

    /* constants */
    public static final String JAR_PATH = "src/test/resources/";

    /* data */
    private RandomAccessFile raf = null;
    private long startOffset = 0L;
    private TFileInfo info = null;

    //debug
    private RandomAccessFile traf = null;
    private String tname = null;

    /* read thread */
    private Thread readTh = null;

    /* queue */
    private BlockingQueue<TFileChunk> queue = new LinkedBlockingQueue<>(1000);
    private BlockingQueue<TFileChunk> tqueue = new LinkedBlockingQueue<>(1000);

    /* take signal */
    private boolean haveTakeAll = false;

    /**
     * open conn to the file
     * @param name
     * @param start
     * @return file info
     * @throws TException
     */
    @Override
    public TFileInfo open(String name, long start) throws TException {
        startOffset = start;
        File file = new File(JAR_PATH + name);
        info = null;
        try {
            raf = new RandomAccessFile(file, "r");

            //debug
            traf = new RandomAccessFile(new File(JAR_PATH + name + "_test"), "rw");
            tname = JAR_PATH + name + "_test";

            info = new TFileInfo();
            info.name = name;
            info.length = file.length();
            info.ts = System.currentTimeMillis();
            info.md5 = CommonUtils.md5Hex(JAR_PATH + name);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        /* start reading */
        reading();


        //debug
        String str = "hello world!";
        byte[] bytes = str.getBytes();
        TFileChunk chunk = new TFileChunk();
        chunk.setBytes(bytes);
        chunk.name = "world!";
        try {
            tqueue.put(chunk);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return info;
    }

    /**
     * reading file to bytes -> chunk -> queue
     */
    private void reading() {
        readTh = new Thread(new Runnable() {
            @Override
            public void run() {
                long ind = startOffset;
                long left = info.length - startOffset; // (length - 1) - startOffset + 1
                int read = transConstants.CHUNK_UNIT;
                if(left < read)
                    read = (int)left;
                //start reading
                try {

                    while (ind <= info.length - 1) {

                        //read
                        byte[] bytes = new byte[read]; // byte length must be accurate
                        raf.read(bytes, 0, read);

                        // make chunk
                        TFileChunk chunk = new TFileChunk();
                        chunk.setBytes(bytes);
                        chunk.setName(info.name);
                        chunk.setLength(read);
                        chunk.setOffset(ind);
                        // queue
                        queue.put(chunk);

                        //debug
//                        byte[] wbytes = new byte[chunk.bytes.remaining()];
//                        chunk.bytes.get(wbytes);
//                        traf.write(wbytes, 0, (int)chunk.length);

                        // index length adjust
                        ind += read;
                        left = info.length - ind;
                        if(left < read)
                            read = (int)left;
                    }

                    // debug
                    //System.out.println("dut: " + CommonUtils.md5Hex(tname));
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
        readTh.start();
    }

    /**
     * get the chunk form the queue
     * @return file chunk
     * @throws TException
     */
    @Override
    public TFileChunk getChunk() throws TException {
        try {
            if(queue.isEmpty())
                return null;
            else {
                TFileChunk chunk = queue.take();

//                //debug
//                byte[] wbytes = new byte[chunk.bytes.remaining()];
//                chunk.bytes.get(wbytes);
//                traf.write(wbytes, 0, (int)chunk.length);   // ByteBuffer.get(bytes) may change the ByteBuffer itself content,
                                                                // ByteBuffer.array() not change itself

                return chunk;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new TException("queue take error!");
        }
    }

    @Override
    public TFileChunk getTChunk() throws TException {
        TFileChunk chunk = null;
        try {
            chunk = tqueue.take();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return chunk;
    }

    /**
     * close conn
     * @throws TException
     */
    @Override
    public void close() throws TException {
        if(raf != null) {
            try {

                //debug
//                System.out.println("dut: " + CommonUtils.md5Hex(tname));

                raf.close();

                //debug
                traf.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
