package network.rpc.core.transport;

import lombok.extern.slf4j.Slf4j;
import network.rpc.api.ServiceSupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestHandlerRegistry {

    private final Map<Integer, RequestHandler> handlers = new HashMap<>();

    private static RequestHandlerRegistry instance = null;

    public static RequestHandlerRegistry getInstance() {
        if (null == instance) {
            instance = new RequestHandlerRegistry();
        }
        return instance;
    }

    private RequestHandlerRegistry() {
        Collection<RequestHandler> handlers = ServiceSupport.loadAll(RequestHandler.class);

        for (RequestHandler handler : handlers) {
            this.handlers.put(handler.type(), handler);

            log.info("Load request handler, type: {}, class: {}.", handler.type(), handler.getClass().getCanonicalName());
        }
    }

    public RequestHandler get(int type) {
        return handlers.get(type);
    }
}