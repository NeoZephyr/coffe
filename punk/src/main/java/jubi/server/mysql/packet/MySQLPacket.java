package jubi.server.mysql.packet;

public abstract class MySQLPacket {

    private int sequenceId;
    protected MySQLPacketType packetType;

    public MySQLPacketType getPacketType() {
        return packetType;
    }
}

