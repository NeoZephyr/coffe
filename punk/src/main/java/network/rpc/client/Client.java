package network.rpc.client;

import lombok.extern.slf4j.Slf4j;
import network.rpc.api.NameService;
import network.rpc.api.RpcAccessPoint;
import network.rpc.api.ServiceSupport;
import network.rpc.service.HelloService;

import java.io.File;
import java.io.IOException;
import java.net.URI;

@Slf4j
public class Client {
    public static void main(String[] args) throws IOException {
        String serviceName = HelloService.class.getCanonicalName();
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDir, "simple_rpc_name_service.data");
        String name = "Master MQ";

        try (RpcAccessPoint accessPoint = ServiceSupport.load(RpcAccessPoint.class)) {
            NameService nameService = accessPoint.getNameService(file.toURI());
            assert nameService != null;
            URI uri = nameService.lookupService(serviceName);
            assert uri != null;

            log.info("find service {}，provider: {}", serviceName, uri);

            HelloService helloService = accessPoint.getRemoteService(uri, HelloService.class);

            log.info("request service, name: {}", name);

            String response = helloService.hello(name);

            log.info("receive response: {}", response);
        }
    }
}