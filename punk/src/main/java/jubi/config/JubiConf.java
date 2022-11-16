package jubi.config;

import com.google.common.collect.Maps;
import jubi.JubiException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JubiConf {

    private final Map<String, String> settings = new ConcurrentHashMap<>();

    public static final String JUBI_CONF_DIR = "JUBI_CONF_DIR";
    public static final String JUBI_CONF_FILE_NAME = "jubi-defaults.conf";
    public static final String JUBI_HOME = "JUBI_HOME";

    public JubiConf set(String key, String value) {
        settings.put(key, value);
        return this;
    }

    public String get(String key) {
        return settings.get(key);
    }

    public JubiConf unset(String key) {
        settings.remove(key);
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
        settings.putAll(sysDefaults);
        return this;
    }

    public JubiConf loadFileDefaults() throws JubiException {
        String confDir = System.getenv().get(JUBI_CONF_DIR);

        if (StringUtils.isBlank(confDir)) {
            String homeDir = System.getenv().get(JUBI_HOME);

            if (StringUtils.isBlank(homeDir)) {
                confDir = homeDir + File.separator + "conf";
            }
        }

        File file = null;

        if (StringUtils.isBlank(confDir)) {
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
                settings.putAll(fileDefaults);
            }
        } catch (IOException e) {
            throw new JubiException("failed to load jubi properties from " + file.getAbsolutePath(), e);
        }

        return this;
    }
}
