package jubi.server.mysql.packet;

public class MySQLErrorPacket extends MySQLPacket {

    public MySQLErrorPacket() {
        this.packetType = MySQLPacket.PACKET_ERROR;
    }
}
