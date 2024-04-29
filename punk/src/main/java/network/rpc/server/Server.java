package network.rpc.server;

import lombok.extern.slf4j.Slf4j;
import network.rpc.api.NameService;
import network.rpc.api.RpcAccessPoint;
import network.rpc.api.ServiceSupport;
import network.rpc.service.HelloService;

import java.io.Closeable;
import java.io.File;
import java.net.URI;

@Slf4j
public class Server {

    public static void main(String[] args) throws Exception {
        String serviceName = HelloService.class.getCanonicalName();
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDir, "simple_rpc_name_service.data");
        HelloService helloService = new HelloServiceImpl();

        try (RpcAccessPoint accessPoint = ServiceSupport.load(RpcAccessPoint.class);
             Closeable ignored = accessPoint.startServer()) {
                NameService nameService = accessPoint.getNameService(file.toURI());
                assert nameService != null;

                URI uri = accessPoint.addServiceProvider(helloService, HelloService.class);
                nameService.registerService(serviceName, uri);
                System.in.read();

                log.info("Bye!");
        }
    }
}
