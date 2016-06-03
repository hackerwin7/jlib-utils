package com.github.hackerwin7.jlib.utils.test.thrift.impl;

import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.TFileChunk;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.TFileInfo;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.TFileService;
import com.github.hackerwin7.jlib.utils.test.thrift.gen.trans.transConstants;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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

    /* read thread */
    private Thread readTh = null;

    /* queue */
    private BlockingQueue<TFileChunk> queue = new LinkedBlockingQueue<>(1000);;

    /* take signal */
    private boolean havaTakeAll = false;

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
            info = new TFileInfo();
            info.name = name;
            info.length = file.length();
            info.ts = System.currentTimeMillis();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        /* start reading */
        reading();

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
                byte[] bytes = new byte[transConstants.CHUNK_UNIT];
                //start reading
                try {
                    while (ind <= info.length - 1 && raf.read(bytes, 0, read) != -1) {
                        // make chunk
                        TFileChunk chunk = new TFileChunk();
                        chunk.bytes = ByteBuffer.wrap(bytes);
                        chunk.name = info.name;
                        chunk.length = read;
                        chunk.offset = ind;
                        // queue
                        queue.put(chunk);
                        // index length adjust
                        ind += read;
                        left = info.length - ind;
                        if(left < read)
                            read = (int)left;
                    }
                } catch (IOException | InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
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
            else
                return queue.take();
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
            throw new TException("queue take error!");
        }
    }

    /**
     * close conn
     * @throws TException
     */
    @Override
    public void close() throws TException {
        if(raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
