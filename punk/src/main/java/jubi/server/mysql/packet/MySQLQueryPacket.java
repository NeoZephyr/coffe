package jubi.server.mysql.packet;

public class MySQLQueryPacket extends MySQLPacket {

    private String sql;

    public MySQLQueryPacket(String sql) {
        this.packetType = MySQLPacket.PACKET_QUERY;
        this.sql = sql;
    }
}
