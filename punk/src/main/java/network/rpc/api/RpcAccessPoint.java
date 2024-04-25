package network.rpc.api;

import java.io.Closeable;
import java.net.URI;
import java.util.Collection;

public interface RpcAccessPoint extends Closeable {

    /**
     * 客户端获取远程服务的引用
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);

    /**
     * 服务端注册服务的实现实例
     */
    <T> URI addServiceProvider(T service, Class<T> serviceClass);

    Closeable startServer() throws Exception;

    /**
     * 获取注册中心的引用
     */
    default NameService getNameService(URI uri) {
        Collection<NameService> services = ServiceSupport.loadAll(NameService.class);

        for (NameService service : services) {
            if (service.supportedProtocols().contains(uri.getScheme())) {
                service.connect(uri);
                return service;
            }
        }

        return null;
    }
}