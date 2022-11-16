package jubi.server.mysql.packet;

public abstract class MySQLPacket {

    public static final String PACKET_PING = "ping";
    public static final String PACKET_QUIT = "quit";
    public static final String PACKET_INIT_DB = "init_db";
    public static final String PACKET_QUERY = "query";
    public static final String PACKET_ERROR = "error";

    private int sequenceId;
    protected String packetType;
}