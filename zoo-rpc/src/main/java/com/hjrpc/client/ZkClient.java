package com.hjrpc.client;

import org.apache.logging.log4j.util.Strings;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZkClient {
    ZooKeeper zooKeeper;
    private String connectString = "zoo1:2181,zoo2:2182,zoo3:2183";
    private int sessionTimeout = 2000;
    public static final String ROOT_PATH = "/servers";
    Map<String,Object> servers = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        ZkClient zkClient = new ZkClient();
        // 获取连接
        zkClient.connectAndWatchServers();
        // 初次访问初始化 服务器列表
        zkClient.fullLoadServers();
        // 业务逻辑
        zkClient.doBusiness();
    }

    private void doBusiness() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void fullLoadServers() throws KeeperException, InterruptedException {
        servers.clear();
        List<String> children = zooKeeper.getChildren(ROOT_PATH , true);
        for (String child : children) {
            byte[] data = zooKeeper.getData(ROOT_PATH+"/"+child, false, null);
            servers.put(child, new String(data));
        }
        System.out.println("_____________________________________");
        for (Map.Entry<String, Object> entry : servers.entrySet()) {
            System.out.println("key:"+entry.getKey()+",value:"+entry.getValue());
        }
        System.out.println("_____________________________________");
    }

    private void connectAndWatchServers() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, (x) -> {
            try {
                fullLoadServers();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
