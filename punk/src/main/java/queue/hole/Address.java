package queue.hole;

import com.google.common.base.Objects;

public class Address {

    public String host;
    public int port;

    public Address(String host, int port) {
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
        Address that = (Address) o;
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
