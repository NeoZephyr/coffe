package jubi.config;

import com.google.common.collect.Maps;
import jubi.JubiException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JubiConf {

    private final Map<String, String> settings = new ConcurrentHashMap<>();

    private static final Object jubiConfUpdateLock = new Object();
    private static volatile Map<String, ConfigEntry<?>> jubiConfEntries = Collections.emptyMap();

    public static final String JUBI_CONF_DIR = "JUBI_CONF_DIR";
    public static final String JUBI_CONF_FILE_NAME = "jubi-defaults.conf";
    public static final String JUBI_HOME = "JUBI_HOME";

    public static final ConfigEntry<Long> ENGINE_OPEN_RETRY_WAIT = buildConf("jubi.session.engine.open.retry.times")
            .doc("How many times retrying to open the engine after failure.")
            .longConf()
            .createWithDefault(10L);

    public static final ConfigEntry<String> SERVER_PRINCIPAL = buildConf("jubi.init.principal")
            .doc("Name of the Kerberos principal.")
            .stringConf()
            .create();

    public static final ConfigEntry<String> HA_ADDRESSES = buildConf("jubi.ha.addresses")
            .doc("The connection string for the discovery ensemble")
            .stringConf()
            .create();

    public static final ConfigEntry<Integer> HA_ZK_CONN_TIMEOUT = buildConf("jubi.ha.zookeeper.connection.timeout")
            .doc("The timeout(ms) of creating the connection to the ZooKeeper ensemble")
            .intConf()
            .createWithDefault(15 * 1000);

    public static final ConfigEntry<Integer> HA_ZK_SESSION_TIMEOUT = buildConf("jubi.ha.zookeeper.session.timeout")
            .doc("The timeout(ms) of a connected session to be idled")
            .intConf()
            .createWithDefault(60 * 1000);

    public static final ConfigEntry<Integer> HA_ZK_CONN_MAX_RETRIES = buildConf("jubi.ha.zookeeper.connection.max.retries")
            .doc("Max retry times for connecting to the ZooKeeper ensemble")
            .intConf()
            .createWithDefault(3);

    public static final ConfigEntry<Integer> HA_ZK_CONN_BASE_RETRY_WAIT = buildConf("jubi.ha.zookeeper.connection.base.retry.wait")
            .doc("Initial amount of time to wait between retries to the ZooKeeper ensemble")
            .intConf()
            .createWithDefault(1000);

    public static final ConfigEntry<Integer> HA_ZK_CONN_MAX_RETRY_WAIT = buildConf("jubi.ha.zookeeper.connection.max.retry.wait")
            .doc("Max amount of time to wait between retries for " +
                    "BOUNDED_EXPONENTIAL_BACKOFF policy can reach, or max time until " +
                    "elapsed for UNTIL_ELAPSED policy to connect the zookeeper ensemble")
            .intConf()
            .createWithDefault(30 * 1000);

    public static ConfigBuilder buildConf(String key) {
        return new ConfigBuilder(key).callback(JubiConf::register);
    }

    public static void register(ConfigEntry<?> entry) {
        synchronized (jubiConfUpdateLock) {
            assert (!containsConfigEntry(entry)) : String.format("Duplicate ConfigEntry. %s has been registered", entry.key());
            HashMap<String, ConfigEntry<?>> updatedMap = new HashMap<>(jubiConfEntries);
            updatedMap.put(entry.key(), entry);
            jubiConfEntries = updatedMap;
        }
    }

    public static void unregister(ConfigEntry<?> entry) {
        synchronized (jubiConfUpdateLock) {
            HashMap<String, ConfigEntry<?>> updatedMap = new HashMap<>(jubiConfEntries);
            updatedMap.remove(entry.key());
            jubiConfEntries = updatedMap;
        }
    }

    public JubiConf set(String key, String value) {
        assert (key != null) : "key cannot be null";
        assert (value != null) : String.format("value cannot be null for key: %s", key);
        settings.put(key, value);
        return this;
    }

    public JubiConf setIfMissing(String key, String value) {
        assert (key != null) : "key cannot be null";
        assert (value != null) : String.format("value cannot be null for key: %s", key);
        settings.putIfAbsent(key, value);
        return this;
    }

    public <T> JubiConf set(ConfigEntry<T> entry, T value) {
        assert (containsConfigEntry(entry)) : String.format("%s is not registered", entry);
        settings.put(entry.key(), entry.convertToText(value));
        return this;
    }

    public <T> JubiConf setIfMissing(ConfigEntry<T> entry, T value) {
        assert (containsConfigEntry(entry)) : String.format("%s is not registered", entry);
        settings.putIfAbsent(entry.key(), entry.convertToText(value));
        return this;
    }

    public String get(String key) {
        return settings.get(key);
    }

    public <T> T get(ConfigEntry<T> entry) {
        String valueText = settings.get(entry.key());

        if (StringUtils.isBlank(valueText)) {
            return entry.defaultValue();
        }

        return entry.convertToValue(valueText);
    }

    public <T> T get(ConfigEntry<T> entry, T defaultValue) {
        String valueText = settings.get(entry.key());

        if (StringUtils.isBlank(valueText)) {
            if (entry.defaultValue() != null) {
                return entry.defaultValue();
            } else {
                return defaultValue;
            }
        }

        return entry.convertToValue(valueText);
    }

    public Map<String, String> getAll() {
        return new HashMap<>(settings);
    }

    public JubiConf unset(String key) {
        settings.remove(key);
        return this;
    }

    public JubiConf unset(ConfigEntry<?> entry) {
        assert (containsConfigEntry(entry)) : String.format("%s is not registered", entry);
        unset(entry.key());
        return this;
    }

    public JubiConf loadFromMap(Map<String, String> props) {
        settings.putAll(props);
        return this;
    }

    public JubiConf loadSysDefaults() {
        Map<String, String> sysDefaults = Maps.fromProperties(System.getProperties())
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith("jubi."))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return loadFromMap(sysDefaults);
    }

    public JubiConf loadFileDefaults() throws JubiException {
        String confDir = System.getenv().get(JUBI_CONF_DIR);

        if (StringUtils.isBlank(confDir)) {
            String homeDir = System.getenv().get(JUBI_HOME);

            if (!StringUtils.isBlank(homeDir)) {
                confDir = homeDir + File.separator + "conf";
            }
        }

        File file = null;

        if (!StringUtils.isBlank(confDir)) {
            file = new File(confDir + File.separator + JUBI_CONF_FILE_NAME);
        } else {
            URL url = Thread.currentThread().getContextClassLoader().getResource(JUBI_CONF_FILE_NAME);

            if (url != null) {
                file = new File(url.getFile());
            }
        }

        if (file == null || !file.exists()) {
            return this;
        }

        try {
            try (InputStreamReader reader = new InputStreamReader(file.toURI().toURL().openStream(), StandardCharsets.UTF_8)) {
                Properties properties = new Properties();
                properties.load(reader);
                Map<String, String> fileDefaults = properties.stringPropertyNames()
                        .stream()
                        .collect(Collectors.toMap(k -> k, k -> properties.getProperty(k).trim()));
                return loadFromMap(fileDefaults);
            }
        } catch (IOException e) {
            throw new JubiException("failed to load jubi properties from " + file.getAbsolutePath(), e);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        JubiConf cloned = new JubiConf();
        settings.forEach(cloned::set);
        return cloned;
    }

    private static ConfigEntry<?> getConfigEntry(String key) {
        return jubiConfEntries.get(key);
    }

    private static Collection<ConfigEntry<?>> getConfigEntries() {
        return jubiConfEntries.values();
    }

    private static boolean containsConfigEntry(ConfigEntry<?> entry) {
        return getConfigEntry(entry.key()) == entry;
    }
}
