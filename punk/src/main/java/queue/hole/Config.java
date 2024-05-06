package queue.hole;

import jubi.config.JubiConf;
import lombok.Data;

@Data
public class Config {
    String host;
    int port;
    boolean serverMode;
    JubiConf conf;

    public Config(String host, int port, boolean serverMode, JubiConf conf) {
        this.host = host;
        this.port = port;
        this.serverMode = serverMode;
        this.conf = conf;
    }
}
