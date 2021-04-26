##### Zookeeper 是什么？

> 分布式服务框架，主要用来解决分布式应用中经常遇到的一些数据管理问题，有监听数据变化功能。如：统一命名服务、状态同步服务、集群管理、分布式应用配置项的管理等。

##### 数据结构

> 树形结构，类似 unix 的文件系统（类似二叉树），每一个节点就是一个 znode

###### znode 的四种类型

PERSISTENT -持久化目录节点：客户端断开连接后，该节点依旧存在

EPHEMERAL-临时目录节点：客户端断开连接后，该节点被删除

PERSISTENT_SEQUENTIAL 和 EPHEMERAL_SEQUENTIAL-序编号目录节点，给该节点名称进行顺序编号

##### 监听机制

> 当节点发生变化（数据改变、被删除、子目录节点增加删除）时，zookeeper 会通知监听的客户端

##### 选举机制

> 半数机制

##### 集群特点

> zookeeper的选举制度，一般情况都是半数以上的原则，所以服务器一般推荐基数个

##### 环境搭建（集群，使用 docker-compose）

```shell
#拉取镜像
docker pull zookeeper
```

```yaml
version: '3.1'

services:
  zoo1:
    image: zookeeper
    restart: always
    hostname: zoo1
    ports: # 对外暴露访问的端口
    - 2181:2181
    environment:
      ZOO_MY_ID: 1 #设置当前机器id
      ZOO_SERVERS: server.1=0.0.0.0:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=zoo3:2888:3888;2181
    volumes:
      - zoo-config1:/conf
      - zoo-data1:/data
      - zoo-datalog1:/datalog
      - /etc/localtime:/etc/localtime:ro
    networks:
      - zoo-cluster
  zoo2:
    image: zookeeper
    restart: always
    hostname: zoo2
    ports:
    - 2182:2181
    environment:
      ZOO_MY_ID: 2
      ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=0.0.0.0:2888:3888;2181 server.3=zoo3:2888:3888;2181
    volumes:
    - zoo-config2:/conf/
    - zoo-data2:/data
    - zoo-datalog2:/datalog
    - /etc/localtime:/etc/localtime:ro
    networks:
    - zoo-cluster
  zoo3:
    image: zookeeper
    restart: always
    hostname: zoo3
    ports:
    - 2183:2181
    environment:
      ZOO_MY_ID: 3
      ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=0.0.0.0:2888:3888;2181
    volumes:
    - zoo-config3:/conf/
    - zoo-data3:/data
    - zoo-datalog3:/datalog
    - /etc/localtime:/etc/localtime:ro
    networks:
    - zoo-cluster
volumes:
  zoo-config1:
  zoo-data1:
  zoo-datalog1:
  zoo-config2:
  zoo-data2:
  zoo-datalog2:
  zoo-config3:
  zoo-data3:
  zoo-datalog3:
networks:
  zoo-cluster:
```

##### 基础使用

```shell
#连接zookeeper
bin/zkCli.sh
connect 192.168.56.102:2181
#关闭客户端连接
close
# 查看所有数据
ls /
# 查看更详细的数据
ls2 /
# 创建数据 -e是临时节点，-s是序号节点，自动带需要
create -e -s /nodename nodevalue
# 读取数据 watch代表监听该数据
get /nodename watch
# 查看节点状态
stat /nodename
# 修改节点值
set /nodename nodevalue2
# 删除节点
delete /nodename
#显示配额
listquota /nodename
```

##### 常见配置

- tickTime：心跳间隔时间，也是后面参数的单位
- initLimit：初始化的超时时间，单位是心跳时间，例配置10，则时间为10*心跳间隔时间
- syncLimit：leader和fellower之间通信超时时间，单位是心跳时间
- dataDir：顾名思义就是 Zookeeper 保存数据的目录，默认情况下，Zookeeper 将写数据的日志文件也保存在这个目录里。
- clientPort：这个端口就是客户端连接 Zookeeper 服务器的端口，Zookeeper 会监听这个端口，接受客户端的访问请求。
- server.A=B：C：D：其中 A 是一个数字，表示这个是第几号服务器；B 是这个服务器的 ip 地址；C 表示的是这个服务器与集群中的 Leader 服务器交换信息的端口；D 表示的是万一集群中的 Leader 服务器挂了，需要一个端口来重新进行选举，选出一个新的 Leader，而这个端口就是用来执行选举时服务器相互通信的端口。如果是伪集群的配置方式，由于 B 都是一样，所以不同的 Zookeeper 实例通信端口号不能一样，所以要给它们分配不同的端口号。

##### 写数据的流程

client 向 server1 写数据，server1 通知 leader 写数据，leader 分别通知集群中的所有服务进行写数据，如果超过半数的机器写成功，则通知server1，server1 再向 client 通知写成功
