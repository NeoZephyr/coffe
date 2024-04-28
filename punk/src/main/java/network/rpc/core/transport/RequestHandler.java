package network.rpc.core.transport;

import network.rpc.core.transport.command.Command;

public interface RequestHandler {
    Command handle(Command command);

    /**
     * 支持的请求类型
     */
    int type();
}