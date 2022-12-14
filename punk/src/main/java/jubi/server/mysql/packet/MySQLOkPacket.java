package jubi.server.mysql.packet;

public class MySQLOkPacket extends MySQLPacket {

    public MySQLOkPacket() {
        this.packetType = MySQLPacketType.OK;
    }
}
