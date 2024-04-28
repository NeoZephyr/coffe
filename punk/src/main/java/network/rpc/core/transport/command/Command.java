package network.rpc.core.transport.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Command {
    protected Header header;
    private byte[] payload;
}