package jubi.server.mysql.packet;

public class MySQLPingPacket extends MySQLPacket {

    public MySQLPingPacket() {
        this.packetType = MySQLPacketType.PING;
    }
}
