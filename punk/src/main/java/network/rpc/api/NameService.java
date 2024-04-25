package network.rpc.api;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;


public interface NameService {

    /**
     * 所有支持的协议
     */
    Collection<String> supportedProtocols();

    /**
     * 连接注册中心
     */
    void connect(URI uri);

    /**
     * 注册服务
     */
    void registerService(String serviceName, URI uri) throws IOException;

    /**
     * 查询服务地址
     */
    URI lookupService(String serviceName) throws IOException;
}