package jubi.service;

import jubi.common.Utils;
import jubi.config.JubiConf;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

import java.io.File;
import java.net.InetSocketAddress;

import static jubi.config.ZookeeperConf.*;

@Slf4j
public class EmbeddedZookeeper extends AbstractService {

    private ZooKeeperServer zks;
    private NIOServerCnxnFactory serverFactory;
    private File dataDirectory;
    private boolean deleteDataDirectoryOnClose = true;
    private String host;

    public EmbeddedZookeeper(String name) {
        super(name);
    }

    @SneakyThrows
    @Override
    public void initialize(JubiConf conf) {
        dataDirectory = new File(conf.get(ZK_DATA_DIR));
        int clientPort = conf.get(ZK_CLIENT_PORT);
        int tickTime = conf.get(ZK_TICK_TIME);
        int maxClientCnxns = conf.get(ZK_MAX_CLIENT_CONNECTIONS);
        int minSessionTimeout = conf.get(ZK_MIN_SESSION_TIMEOUT);
        int maxSessionTimeout = conf.get(ZK_MAX_SESSION_TIMEOUT);
        host = conf.get(ZK_CLIENT_PORT_ADDRESS);
        zks = new ZooKeeperServer(dataDirectory, dataDirectory, tickTime);
        zks.setMinSessionTimeout(minSessionTimeout);
        zks.setMaxSessionTimeout(maxSessionTimeout);
        serverFactory = new NIOServerCnxnFactory();
        serverFactory.configure(new InetSocketAddress(host, clientPort), maxClientCnxns);
        super.initialize(conf);
    }

    @SneakyThrows
    @Override
    public synchronized void start() {
        // NIOServerCnxnFactory 会创建 NIOServerCnxn 实例来处理请求，然后将请求传递给 ZooKeeperServer 进行处理
        serverFactory.startup(zks);
        log.info(String.format("%s is started at %s", getName(), getConnectString()));
        Utils.addShutdownHook(this::stop, Utils.DEFAULT_SHUTDOWN_PRIORITY);
        super.start();
    }

    @Override
    public void stop() {
        if (getState() == ServiceState.STARTED) {
            if (serverFactory != null) {
                serverFactory.shutdown();
            }
            if (zks != null) {
                zks.shutdown();
            }
            if (deleteDataDirectoryOnClose) {
                Utils.deleteDirectoryRecursively(dataDirectory);
            }
        }
        super.stop();
    }

    private synchronized String getConnectString() {
        return String.format("%s:%s", host, serverFactory.getLocalPort());
    }

}
