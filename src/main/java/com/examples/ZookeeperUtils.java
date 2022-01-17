package com.examples;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperUtils {
    public static void printChildren(String zpath, ZooKeeper zk) {
        try {
            zk.getChildren(zpath, false).forEach(System.out::println);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String createIfNotExists(String zpath, ZooKeeper zk, Watcher watcher) throws InterruptedException, KeeperException {
        if (zk.exists(zpath, watcher) == null) {
            return zk.create(zpath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        return null;
    }
}
