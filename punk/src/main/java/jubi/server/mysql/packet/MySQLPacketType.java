package jubi.server.mysql.packet;

public enum MySQLPacketType {

    PING,
    QUIT,
    INIT_DB,
    QUERY,
    ERROR,
    OK
}
