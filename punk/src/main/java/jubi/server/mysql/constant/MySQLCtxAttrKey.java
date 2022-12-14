package jubi.server.mysql.constant;

import io.netty.util.AttributeKey;
import jubi.session.SessionKey;

import static io.netty.util.AttributeKey.valueOf;

public interface MySQLCtxAttrKey {

    AttributeKey<Integer> CONNECTION_ID = valueOf("CONNECTION_ID");
    AttributeKey<String> REMOTE_IP = valueOf("REMOTE_IP");
    AttributeKey<String> DATABASE = valueOf("DATABASE");
    AttributeKey<String> USER = valueOf("USER");
    AttributeKey<SessionKey> SESSION_KEY = valueOf("SESSION_KEY");
}
