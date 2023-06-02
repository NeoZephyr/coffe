package jubi.netty;

public class RpcEndpoint {

    public RpcAddress address;
    public String name;

    public RpcEndpoint(RpcAddress address, String name) {
        this.address = address;
        this.name = name;
    }

    public RpcEndpoint(String host, int port, String name) {
        this.address = new RpcAddress(host, port);
        this.name = name;
    }

}
