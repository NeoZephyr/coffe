package jubi.netty.core;

import jubi.config.JubiConf;
import lombok.Data;

@Data
public class RpcEnvConfig {
    String host;
    int port;
    boolean serverMode;
    JubiConf conf;

    public RpcEnvConfig(String host, int port, boolean serverMode, JubiConf conf) {
        this.host = host;
        this.port = port;
        this.serverMode = serverMode;
        this.conf = conf;
    }
}
