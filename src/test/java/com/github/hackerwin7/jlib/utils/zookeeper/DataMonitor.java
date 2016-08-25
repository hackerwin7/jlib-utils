package com.github.hackerwin7.jlib.utils.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: hackerwin7
 * Date: 2016/08/25
 * Time: 5:14 PM
 * Desc:
 * Tips:
 */
public class DataMonitor implements Watcher, AsyncCallback.StatCallback {
    ZooKeeper zk;
    String znode;
    Watcher chainedWatcher;
    boolean dead;
    DataMonitorListener listener;
    byte prevData[];

    public DataMonitor(ZooKeeper zk, String znode, Watcher chainedWatcher, DataMonitorListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.chainedWatcher = chainedWatcher;
        this.listener = listener;
        zk.exists(znode, true, this, null);
    }

    public interface DataMonitorListener {
        void exists(byte data[]);
        void closing(int rc);
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        boolean exists;
        KeeperException.Code code = KeeperException.Code.get(rc);
        switch (code) {
            case OK:
                exists = true;
                break;
            case NONODE:
                exists = false;
                break;
            case SESSIONEXPIRED:
            case NOAUTH:
                dead = true;
                listener.closing(rc);
                return;
            default:
                zk.exists(znode, true, this, null);
                return;
        }
        byte b[] = null;
        if(exists) {
            try {
                b = zk.getData(znode, false, null);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                return;
            }
        }
        if((b == null && b != prevData) || (b != null && !Arrays.equals(prevData, b))) {
            listener.exists(b);
            prevData = b;
        }
    }

    @Override
    public void process(WatchedEvent event) {
        String path = event.getPath();
        if(event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected:
                    break;
                case Expired:
                    dead = true;
                    listener.closing(KeeperException.Code.SESSIONEXPIRED.intValue());
                    break;
            }
        } else {
            if(path != null && path.equals(znode)) {
                zk.exists(znode, true, this, null);
            }
        }
        if(chainedWatcher != null) {
            chainedWatcher.process(event);
        }
    }
}
