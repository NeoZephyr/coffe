package jubi.server.mysql.packet;

public class MySQLQuitPacket extends MySQLPacket {

    public MySQLQuitPacket() {
        this.packetType = MySQLPacketType.QUIT;
    }
}
