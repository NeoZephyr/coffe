package jubi.netty;

import com.google.common.base.Objects;

public class RpcAddress {
    public String host;
    public int port;

    public RpcAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String hostPort() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcAddress that = (RpcAddress) o;
        return port == that.port && Objects.equal(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(host, port);
    }

    @Override
    public String toString() {
        return hostPort();
    }
}
