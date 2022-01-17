package com.examples;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;

public class ZookeeperTest {

    public static void main(String[] args) throws IOException {
        String hostPort = "localhost:2181";
        String zpath = "/";
        ZooKeeper zk = new ZooKeeper(hostPort, 2000, null);
        ZookeeperUtils.printChildren(zpath, zk);
    }

}
