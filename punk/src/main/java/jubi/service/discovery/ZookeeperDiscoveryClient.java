package jubi.service.discovery;

import jubi.JubiException;
import jubi.common.ThreadUtils;
import jubi.config.JubiConf;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.*;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ZookeeperDiscoveryClient {

    private JubiConf conf;
    private CuratorFramework client;
    private volatile PersistentNode node;

    private static final ScheduledExecutorService connectionChecker = ThreadUtils.newDaemonSingleThreadScheduledExecutor("zk-connection-checker", true);

    public ZookeeperDiscoveryClient(JubiConf conf) {
        this.conf = conf;
        client = buildClient();
    }

    public void start() {
        client.start();
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }

    public String createPath(String path, String mode, boolean createParent) throws Exception {
        if (createParent) {
            return client.create().creatingParentsIfNeeded().withMode(CreateMode.valueOf(mode)).forPath(path);
        } else {
            return client.create().withMode(CreateMode.valueOf(mode)).forPath(path);
        }
    }

    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public void setData(String path, byte[] data) throws Exception {
        client.setData().forPath(path, data);
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public boolean pathExists(String path) throws Exception {
        return client.checkExists().forPath(path) != null;
    }

    public void deletePath(String path, boolean deleteChildren) throws Exception {
        if (deleteChildren) {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } else {
            client.delete().forPath(path);
        }
    }

    public void monitorState() {
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            private final AtomicBoolean isConnected = new AtomicBoolean(false);

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                log.info("zookeeper client connection state changed to: {}", newState);

                switch (newState) {
                    case CONNECTED:
                    case RECONNECTED:
                        isConnected.set(true);
                        break;
                    case LOST:
                        isConnected.set(false);
                        connectionChecker.schedule(() -> {
                            if (!isConnected.get()) {
                                log.error("Zookeeper client connection state changed to: {}. Give up retry and stop gracefully.", newState);
                                // stopGracefully
                            }
                        }, getGracefulStopThreadDelay(conf), TimeUnit.MILLISECONDS);
                    default:
                }
            }
        });
    }

    public <T> T tryLock(String path, long timeout, Callable<T> callable) throws JubiException {
        InterProcessSemaphoreMutex mutex = new InterProcessSemaphoreMutex(client, path);

        try {
            boolean acquire = mutex.acquire(timeout, TimeUnit.MILLISECONDS);

            if (!acquire) {
                throw new JubiException(String.format("Timeout to lock on path [%s] after %d ms.", path, timeout));
            }

            return callable.call();
        } catch (Exception e) {
            throw new JubiException(String.format("Lock failed on path [%s]", path), e);
        } finally {
            try {
                mutex.release();
            } catch (Exception ignored) {}
        }
    }

    private CuratorFramework buildClient() {
        String connectionStr = conf.get(JubiConf.HA_ADDRESSES);
        Integer sessionTimeout = conf.get(JubiConf.HA_ZK_SESSION_TIMEOUT);
        Integer connectionTimeout = conf.get(JubiConf.HA_ZK_CONN_TIMEOUT);
        Integer baseRetryWait = conf.get(JubiConf.HA_ZK_CONN_BASE_RETRY_WAIT);
        Integer maxRetryWait = conf.get(JubiConf.HA_ZK_CONN_MAX_RETRY_WAIT);
        Integer maxRetries = conf.get(JubiConf.HA_ZK_CONN_MAX_RETRIES);
        RetryPolicyKind policyKind = RetryPolicyKind.valueOf(conf.get(JubiConf.HA_ZK_CONN_RETRY_POLICY_KIND));
        RetryPolicy retryPolicy;

        switch (policyKind) {
            case ONE_TIME:
                retryPolicy = new RetryOneTime(baseRetryWait);
                break;
            case N_TIME:
                retryPolicy = new RetryNTimes(maxRetries, baseRetryWait);
                break;
            case BOUNDED_EXPONENTIAL_BACKOFF:
                retryPolicy = new BoundedExponentialBackoffRetry(baseRetryWait, maxRetryWait, maxRetries);
                break;
            case UNTIL_ELAPSED:
                retryPolicy = new RetryUntilElapsed(maxRetryWait, baseRetryWait);
                break;
            default:
                retryPolicy = new ExponentialBackoffRetry(baseRetryWait, maxRetries);
        }

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(connectionStr)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(retryPolicy);
        return builder.build();
    }

    private long getGracefulStopThreadDelay(JubiConf conf) {
        Integer baseWait = conf.get(JubiConf.HA_ZK_CONN_BASE_RETRY_WAIT);
        Integer maxWait = conf.get(JubiConf.HA_ZK_CONN_MAX_RETRY_WAIT);
        Integer maxRetries = conf.get(JubiConf.HA_ZK_CONN_MAX_RETRIES);
        RetryPolicyKind policyKind = RetryPolicyKind.valueOf(conf.get(JubiConf.HA_ZK_CONN_RETRY_POLICY_KIND));

        long delay = 0;

        switch (policyKind) {
            case ONE_TIME:
                delay = baseWait;
                break;
            case N_TIME:
                delay = (long) maxRetries * baseWait;
                break;
            case BOUNDED_EXPONENTIAL_BACKOFF:
                for (int i = 0; i < maxRetries; ++i) {
                    int retryWait = baseWait * Math.max(1, new Random().nextInt(1 << (i + 1)));
                    delay += Math.min(retryWait, maxWait);
                }
                break;
            case UNTIL_ELAPSED:
                delay = maxWait;
                break;
            case EXPONENTIAL_BACKOFF:
                for (int i = 0; i < maxRetries; ++i) {
                    int retryWait = baseWait * Math.max(1, new Random().nextInt(1 << (i + 1)));
                    delay += retryWait;
                }
                break;
            default:
        }

        return delay;
    }
}
