package jubi.server.mysql;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jubi.server.mysql.constant.MySQLCtxAttrKey;
import jubi.server.mysql.packet.*;
import jubi.session.SessionKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import static jubi.server.mysql.constant.MySQLCtxAttrKey.CONNECTION_ID;
import static jubi.server.mysql.constant.MySQLCtxAttrKey.SESSION_KEY;

@Slf4j
public class MySQLHandler extends SimpleChannelInboundHandler<MySQLPacket> {

    private String connectionUrl;
    private ThreadPoolExecutor execPool;
    private volatile boolean closed = false;
    private Map<Integer, SessionKey> connIdToSessionKey = new ConcurrentHashMap<>();

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeSession(ctx);
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MySQLPacket packet) throws Exception {
        ensureSessionOpened(ctx);

        List<MySQLPacket> response;

        if (packet.getPacketType() == MySQLPacketType.PING) {
            response = handlePing(ctx, (MySQLPingPacket) packet);
        } else if (packet.getPacketType() == MySQLPacketType.QUIT) {
            response = handleQuit(ctx, (MySQLQuitPacket) packet);
        } else if (packet.getPacketType() == MySQLPacketType.INIT_DB) {
            response = handleInitDb(ctx, (MySQLInitDbPacket) packet);
        } else if (packet.getPacketType() == MySQLPacketType.QUERY) {
            response = handleQuery(ctx, (MySQLQueryPacket) packet);
        } else {
            throw new UnsupportedOperationException(packet.getPacketType().toString());
        }

        for (MySQLPacket responsePacket : response) {
            ctx.channel().write(responsePacket);
        }

        ctx.channel().flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Integer connectionId = ctx.channel().attr(MySQLCtxAttrKey.CONNECTION_ID).get();
        MySQLErrorPacket errorPacket = new MySQLErrorPacket();

        log.error("connection: {}, error packet: {}", connectionId, errorPacket);

        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(errorPacket);
        } else {
            log.warn("Ignore error package for connection: {}", connectionId);
        }
    }

    private SessionKey openSession(ChannelHandlerContext ctx) {
        String user = ctx.channel().attr(MySQLCtxAttrKey.USER).get();
        String remoteIp = ctx.channel().attr(MySQLCtxAttrKey.REMOTE_IP).get();
        String database = ctx.channel().attr(MySQLCtxAttrKey.DATABASE).get();

        return new SessionKey("");
    }

    private void closeSession(ChannelHandlerContext ctx) {
        synchronized (this) {
            if (!closed) {
                SessionKey sessionKey = ctx.channel().attr(SESSION_KEY).get();
                ctx.channel().attr(SESSION_KEY).set(null);
                Integer connectionId = ctx.channel().attr(MySQLCtxAttrKey.CONNECTION_ID).getAndSet(null);
                closed = true;
                connIdToSessionKey.remove(connectionId);
            }
        }
    }

    private void ensureSessionOpened(ChannelHandlerContext ctx) {
        if (ctx.channel().attr(SESSION_KEY).get() == null) {
            synchronized (this) {
                if (ctx.channel().attr(SESSION_KEY).get() == null) {
                    SessionKey sessionKey = openSession(ctx);
                    ctx.channel().attr(SESSION_KEY).set(sessionKey);
                    Integer connectionId = ctx.channel().attr(CONNECTION_ID).get();
                    connIdToSessionKey.put(connectionId, sessionKey);
                }
            }
        }
    }

    List<MySQLPacket> handlePing(ChannelHandlerContext ctx, MySQLPingPacket packet) {
        return Collections.singletonList(new MySQLOkPacket());
    }

    List<MySQLPacket> handleInitDb(ChannelHandlerContext ctx, MySQLInitDbPacket packet) {
        // use packet.getDatabase()
        return Collections.singletonList(new MySQLOkPacket());
    }

    List<MySQLPacket> handleQuit(ChannelHandlerContext ctx, MySQLQuitPacket packet) {
        closeSession(ctx);
        return Collections.singletonList(new MySQLOkPacket());
    }

    List<MySQLPacket> handleQuery(ChannelHandlerContext ctx, MySQLQueryPacket packet) {
        return null;
    }

//        def handleQuery(
//                ctx: ChannelHandlerContext,
//                pkg: MySQLComQueryPacket): Seq[MySQLPacket] = {
//                executeStatement(ctx, pkg.sql).toPackets
//        }

//        private def beExecuteStatement(ctx: ChannelHandlerContext, sql: String): MySQLQueryResult = {
//        try {
//            val ssHandle = ctx.channel.attr(SESSION_HANDLE).get
//            val opHandle = be.executeStatement(
//                    ssHandle,
//                    sql,
//                    confOverlay = Map.empty,
//                    runAsync = false,
//                    queryTimeout = 0)
//            val opStatus = be.getOperationStatus(opHandle)
//            if (opStatus.state != FINISHED) {
//                throw opStatus.exception
//                        .getOrElse(KyuubiSQLException(s"Error operator state ${opStatus.state}"))
//            }
//            val tableSchema = be.getResultSetMetadata(opHandle)
//            val rowSet = be.fetchResults(
//                    opHandle,
//                    FetchOrientation.FETCH_NEXT,
//                    Int.MaxValue,
//                    fetchLog = false)
//            MySQLQueryResult(tableSchema, rowSet)
//        } catch {
//            case rethrow: Exception =>
//                warn("Error executing statement: ", rethrow)
//                throw rethrow
//        }
//  }
//    }
//
//        def executeStatement(ctx: ChannelHandlerContext, sql: String): MySQLQueryResult = {
//                val newSQL = MySQLDialectHelper.convertQuery(sql)
//        if (sql != newSQL) debug(s"Converted to $newSQL")
//
//        if (MySQLDialectHelper.shouldExecuteLocal(newSQL)) {
//            MySQLDialectHelper.localExecuteStatement(ctx, newSQL)
//        } else {
//            beExecuteStatement(ctx, newSQL)
//        }
//  }
}
