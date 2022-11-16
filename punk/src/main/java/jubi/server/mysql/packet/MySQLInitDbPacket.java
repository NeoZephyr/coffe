package jubi.server.mysql.packet;

public class MySQLInitDbPacket extends MySQLPacket {

    public MySQLInitDbPacket() {
        this.packetType = MySQLPacket.PACKET_INIT_DB;
    }
}
