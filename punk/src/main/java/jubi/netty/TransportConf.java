package jubi.netty;

import jubi.config.ConfigEntry;
import jubi.config.JubiConf;
import common.IOMode;

import static jubi.config.JubiConf.buildConf;

public class TransportConf {

    public static final ConfigEntry<String> NETTY_IO_MODE = buildConf("jubi.client.netty.io.mode")
            .doc("Netty EventLoopGroup backend, available options: NIO, EPOLL.")
            .stringConf()
            .createWithDefault(IOMode.NIO.name());

    public static final ConfigEntry<Integer> NETTY_IO_CONNECT_TIMEOUT_MS = buildConf("jubi.client.netty.io.connect.timeout.ms")
            .doc("netty connect to server time out mills")
            .intConf()
            .createWithDefault(10 * 1000);

    public static final ConfigEntry<Integer> NETTY_IO_CONNECTION_ACTIVE_TIMEOUT_MS = buildConf("jubi.client.netty.client.connection.timeout.ms")
            .doc("connection active timeout")
            .intConf()
            .createWithDefault(10 * 60 * 1000);

    public static final ConfigEntry<Boolean> NETTY_IO_CONNECTION_IDLE_CLOSE = buildConf("jubi.client.netty.client.connection.idle.close")
            .doc("")
            .booleanConf()
            .createWithDefault(true);

    public static final ConfigEntry<Integer> NETTY_IO_CONNECT_RETRY_WAIT_MS = buildConf("jubi.client.netty.client.connect.retry.wait.ms")
            .doc("connect retry wait time")
            .intConf()
            .createWithDefault(5 * 1000);

    public static final ConfigEntry<Integer> NETTY_CLIENT_THREAD_NUM = buildConf("jubi.client.netty.client.threads")
            .doc("Number of threads used in the client thread pool.")
            .intConf()
            .createWithDefault(1);

    public static final ConfigEntry<Integer> NETTY_SERVER_THREAD_NUM = buildConf("jubi.server.netty.server.threads")
            .doc("Number of threads used in the server thread pool.")
            .intConf()
            .createWithDefault(1);

    public static final ConfigEntry<Integer> NETTY_SERVER_BACKLOG = buildConf("jubi.server.netty.backlog")
            .doc("")
            .intConf()
            .createWithDefault(-1);

    public static final ConfigEntry<Boolean> NETTY_CLIENT_ENABLE_KEEP_ALIVE = buildConf("jubi.client.netty.keep.alive")
            .doc("")
            .booleanConf()
            .createWithDefault(false);

    public static final ConfigEntry<Boolean> NETWORK_CLIENT_PREFER_DIRECT_BUFFER = buildConf("jubi.client.netty.client.prefer.direct.bufs")
            .doc("If true, we will prefer allocating off-heap byte buffers within Netty.")
            .booleanConf()
            .createWithDefault(true);

    public static final ConfigEntry<Integer> NETTY_CLIENT_NUM_CONNECTIONS_PER_PEER = buildConf("jubi.client.netty.client.connections.per.peer")
            .doc("Number of concurrent connections between two nodes.")
            .intConf()
            .createWithDefault(2);

    public static final ConfigEntry<Integer> NETTY_CLIENT_RECEIVE_BUFFER = buildConf("jubi.client.netty.client.receive.buffer")
            .doc("Receive buffer size (SO_RCVBUF). Note: the optimal size for receive buffer and send buffer "
                    + "should be latency * network_bandwidth. Assuming latency = 1ms, network_bandwidth = 10Gbps "
                    + "buffer size should be ~ 1.25MB.")
            .intConf()
            .createWithDefault(0);

    public static final ConfigEntry<Integer> NETTY_CLIENT_SEND_BUFFER = buildConf("jubi.client.netty.client.send.buffer")
            .doc("Send buffer size (SO_SNDBUF).")
            .intConf()
            .createWithDefault(0);

    public static final ConfigEntry<Integer> NETTY_DISPATCHER_NUM_THREADS = buildConf("")
            .doc("")
            .intConf()
            .create();

    private final JubiConf conf;

    public TransportConf(JubiConf conf) {
        this.conf = conf;
    }

    public IOMode ioMode() {
        return IOMode.valueOf(conf.get(NETTY_IO_MODE));
    }

    public int connectRetryWaitMs() {
        return conf.get(NETTY_IO_CONNECT_RETRY_WAIT_MS);
    }

    public int connectTimeoutMs() {
        return conf.get(NETTY_IO_CONNECT_TIMEOUT_MS);
    }

    public int connectionActiveTimeoutMs() {
        return conf.get(NETTY_IO_CONNECTION_ACTIVE_TIMEOUT_MS);
    }

    public boolean closeIdleConnections() {
        return conf.get(NETTY_IO_CONNECTION_IDLE_CLOSE);
    }

    public int clientThreads() {
        return conf.get(NETTY_CLIENT_THREAD_NUM);
    }

    public int numConnectionsPerPeer() {
        return conf.get(NETTY_CLIENT_NUM_CONNECTIONS_PER_PEER);
    }

    public boolean preferDirectBuffer() {
        return conf.get(NETWORK_CLIENT_PREFER_DIRECT_BUFFER);
    }

    public int receiveBuf() {
        return conf.get(NETTY_CLIENT_RECEIVE_BUFFER);
    }

    public int sendBuf() {
        return conf.get(NETTY_CLIENT_SEND_BUFFER);
    }

    public int serverThreads() {
        return conf.get(NETTY_SERVER_THREAD_NUM);
    }

    public int backlog() {
        return conf.get(NETTY_SERVER_BACKLOG);
    }

    public boolean enableKeepAlive() {
        return conf.get(NETTY_CLIENT_ENABLE_KEEP_ALIVE);
    }
}
