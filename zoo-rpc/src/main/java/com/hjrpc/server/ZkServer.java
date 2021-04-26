package com.hjrpc.server;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZkServer {
    ZooKeeper zooKeeper;
    private String connectString = "zoo1:2181,zoo2:2182,zoo3:2183";
    private int sessionTimeout = 2000;
    public static final String ROOT_PATH = "/servers";
    private static String serverName;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        serverName = args[0];
        ZkServer zkServer = new ZkServer();
        System.out.println(serverName+" server starting ...");
        // 获取连接
        zkServer.connect();
        // 注册服务
        zkServer.register();
        // 业务逻辑
        zkServer.doBusiness();
    }

    private void doBusiness() throws InterruptedException {
        System.out.println(serverName+" server is online!");
        Thread.sleep(Long.MAX_VALUE);
    }

    private void register() throws KeeperException, InterruptedException {
        if (zooKeeper.exists(ROOT_PATH, false) == null) {
            zooKeeper.create(ROOT_PATH, "servers".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        zooKeeper.create(ROOT_PATH + "/server", serverName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    private void connect() throws IOException {
        zooKeeper = new ZooKeeper(connectString, sessionTimeout, (x) -> {

        });
    }
}
