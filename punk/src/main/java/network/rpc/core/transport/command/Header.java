package network.rpc.core.transport.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Header {
    private int requestId;
    private int version;
    private int type;

    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES;
    }
}
