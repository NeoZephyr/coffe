package jubi.server.mysql.packet;

public class MySQLQueryPacket extends MySQLPacket {

    private String sql;

    public MySQLQueryPacket(String sql) {
        this.packetType = MySQLPacketType.QUERY;
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
