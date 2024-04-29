package network.rpc.core.client.stubs;

import network.rpc.core.client.RequestIdSupport;
import network.rpc.core.client.ServiceStub;
import network.rpc.core.client.ServiceTypes;
import network.rpc.core.serialize.SerializeSupport;
import network.rpc.core.transport.Transport;
import network.rpc.core.transport.command.Code;
import network.rpc.core.transport.command.Command;
import network.rpc.core.transport.command.Header;
import network.rpc.core.transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;

public abstract class AbstractStub implements ServiceStub {

    protected Transport transport;

    protected byte [] invokeRemote(RpcRequest request) {
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte [] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);

        try {
            Command responseCommand = transport.send(requestCommand).get();
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();

            if(responseHeader.getCode() == Code.SUCCESS.getCode()) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
