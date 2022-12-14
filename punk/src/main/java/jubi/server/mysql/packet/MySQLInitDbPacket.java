package jubi.server.mysql.packet;

public class MySQLInitDbPacket extends MySQLPacket {

    private String database;

    public MySQLInitDbPacket(String database) {
        this.packetType = MySQLPacketType.INIT_DB;
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }
}
