package jubi.config;

import static jubi.config.JubiConf.buildConf;

public class ZookeeperConf {

    public static final ConfigEntry<String> ZK_DATA_DIR = buildConf("jubi.zookeeper.embedded.data.dir")
            .doc("dataDir for the embedded zookeeper server where stores the in-memory database " +
                    "snapshots and, unless specified otherwise, the transaction log of updates to the database.")
            .stringConf()
            .create();

    public static final ConfigEntry<Integer> ZK_CLIENT_PORT = buildConf("jubi.zookeeper.embedded.client.port")
            .doc("clientPort for the embedded ZooKeeper server to listen for client connections, " +
                    "a client here could be Kyuubi server, engine, and JDBC client")
            .intConf()
            .createWithDefault(2181);

    public static final ConfigEntry<String> ZK_CLIENT_PORT_ADDRESS = buildConf("jubi.zookeeper.embedded.client.port.address")
            .doc("clientPortAddress for the embedded ZooKeeper server to")
            .stringConf()
            .create();

    public static final ConfigEntry<Integer> ZK_TICK_TIME = buildConf("jubi.zookeeper.embedded.tick.time")
            .doc("tickTime in milliseconds for the embedded ZooKeeper server")
            .intConf()
            .createWithDefault(3000);

    public static final ConfigEntry<Integer> ZK_MAX_CLIENT_CONNECTIONS = buildConf("jubi.zookeeper.embedded.max.client.connections")
            .doc("maxClientCnxns for the embedded ZooKeeper server to limit the number of concurrent " +
                    "connections of a single client identified by IP address")
            .intConf()
            .createWithDefault(120);

    public static final ConfigEntry<Integer> ZK_MIN_SESSION_TIMEOUT = buildConf("jubi.zookeeper.embedded.min.session.timeout")
            .doc("minSessionTimeout in milliseconds for the embedded ZooKeeper server will allow the" +
                    " client to negotiate. Defaults to 2 times the tickTime")
            .intConf()
            .createWithDefault(3000 * 2);

    public static final ConfigEntry<Integer> ZK_MAX_SESSION_TIMEOUT = buildConf("jubi.zookeeper.embedded.max.session.timeout")
            .doc("maxSessionTimeout in milliseconds for the embedded ZooKeeper server will allow the" +
                    " client to negotiate. Defaults to 20 times the tickTime")
            .intConf()
            .createWithDefault(3000 * 20);
}
