package com.examples;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperWatcherTest {

    private static final String hostPort = "localhost:2181";
    private static final String zooDataPath = "/WatcherTest";

    static class TestWatcher implements Watcher, Runnable {

        private final ZooKeeper zooKeeper;
        private final String zpath;

        TestWatcher(ZooKeeper zooKeeper, String zpath) {
            this.zooKeeper = zooKeeper;
            this.zpath = zpath;
            zooKeeper.register(this);
        }

        @Override
        public void process(WatchedEvent event) {
            System.out.printf("Watcher Event Received: %s%n", event.toString());
            try {
                switch (event.getType()) {
                    case NodeCreated -> System.out.println("Node created: "
                            + new String(zooKeeper.getData(zpath, this, null)));
                    case NodeDataChanged -> System.out.println("Node data changed: "
                            + new String(zooKeeper.getData(zpath, this, null)));
                    case NodeDeleted -> System.out.println("Node deleted: " + zpath);
                    default -> System.out.println("unhandled event: " + event.getType());
                }
                this.zooKeeper.register(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Watcher exiting");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper(hostPort, 2000, null);
        TestWatcher watcher = new TestWatcher(zooKeeper, zooDataPath);
        Thread watcherThread = new Thread(watcher);
        watcherThread.start();

        Thread.sleep(3_000);
        System.out.println("Creating znode : " + zooDataPath);
        ZookeeperUtils.createIfNotExists(zooDataPath, zooKeeper, watcher);
        Thread.sleep(3_000);
        System.out.println("changing znode data: " + zooDataPath);
        zooKeeper.setData(zooDataPath, "NewData".getBytes(StandardCharsets.UTF_8), -1);
        Thread.sleep(3_000);
        System.out.println("Deleting znode : " + zooDataPath);
        zooKeeper.delete(zooDataPath, -1);
        Thread.sleep(3_000);

        watcherThread.interrupt();
        watcherThread.join();
        zooKeeper.close();
        System.out.println("Exiting");
    }
}
