package network.rpc.core.server;

import lombok.extern.slf4j.Slf4j;
import network.rpc.api.Sharable;
import network.rpc.core.client.ServiceTypes;
import network.rpc.core.client.stubs.RpcRequest;
import network.rpc.core.serialize.SerializeSupport;
import network.rpc.core.transport.RequestHandler;
import network.rpc.core.transport.command.Code;
import network.rpc.core.transport.command.Command;
import network.rpc.core.transport.command.Header;
import network.rpc.core.transport.command.ResponseHeader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Sharable
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {

    private Map<String, Object> serviceProviders = new HashMap<>();

    @Override
    public Command handle(Command command) {
        Header header = command.getHeader();
        RpcRequest rpcRequest = SerializeSupport.parse(command.getPayload());
        Object serviceProvider = serviceProviders.get(rpcRequest.getServiceName());

        if (serviceProvider != null) {
            String arg = SerializeSupport.parse(rpcRequest.getArgs());

            try {
                Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
                String result = (String) method.invoke(serviceProvider, arg);
                return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId()), SerializeSupport.serialize(result));
            } catch (Exception e) {
                log.warn("Exception:", e);
                return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.UNKNOWN_ERROR.getCode(), e.getMessage()), new byte[0]);
            }
        }

        log.warn("No service provider of {}#{}(String)", rpcRequest.getServiceName(), rpcRequest.getMethodName());
        return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.NO_PROVIDER.getCode(), "No service provider"), new byte[0]);
    }

    @Override
    public <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
        log.info("add service: {}, provider: {}", serviceClass.getCanonicalName(), serviceProvider.getClass().getCanonicalName());
    }

    @Override
    public int type() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }
}
