package com.hjrpc;

import org.apache.zookeeper.*;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ZooBaseTest {

    ZooKeeper client = null;
    @After
    public void destory(){
        if(client!=null){
            try {
                client.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Before
    public void init() throws IOException {
        String connectString = "zoo1:2181,zoo2:2182,zoo3:2183";
        client = new ZooKeeper(connectString, 2000, (x)->{
            List<String> children = null;
            try {
                children = client.getChildren("/",true);
                System.out.println("监听到数据:"+children);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testCreate() throws KeeperException, InterruptedException {
        String s = client.create("/test2", "hah".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s);
    }

    @Test
    public void testWatch() throws KeeperException, InterruptedException {
        List<String> children = client.getChildren("/", true);
        System.out.println("获取到数据:"+children);
        Thread.sleep(20000);
    }

    @Test
    public void checkNodeExists() throws KeeperException, InterruptedException {
        Stat exists = client.exists("/test3", true);
        System.out.println(exists==null?"not exists":"exists");
    }



}
