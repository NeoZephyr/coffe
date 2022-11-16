package jubi.server.mysql.constant;

import io.netty.util.AttributeKey;

import static io.netty.util.AttributeKey.valueOf;

public interface MySQLCtxAttrKey {

    AttributeKey<Integer> CONNECTION_ID = valueOf("CONNECTION_ID");
    AttributeKey<String> REMOTE_IP = valueOf("REMOTE_IP");
    AttributeKey<String> DATABASE = valueOf("DATABASE");
}
