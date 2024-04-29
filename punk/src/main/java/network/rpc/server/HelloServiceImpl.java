package network.rpc.server;

import lombok.extern.slf4j.Slf4j;
import network.rpc.service.HelloService;

@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        log.info("HelloServiceImpl receive: {}", name);

        String ret = "Hello, " + name;

        log.info("HelloServiceImpl response: {}", ret);

        return ret;
    }
}
