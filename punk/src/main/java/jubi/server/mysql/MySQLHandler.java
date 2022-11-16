package jubi.server.mysql;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jubi.server.mysql.constant.MySQLCtxAttrKey;
import jubi.server.mysql.packet.MySQLErrorPacket;
import jubi.server.mysql.packet.MySQLPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class MySQLHandler extends SimpleChannelInboundHandler<MySQLPacket> {

    private String connectionUrl;
    private ThreadPoolExecutor execPool;
    private volatile boolean closed = false;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeSession(ctx);
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MySQLPacket msg) throws Exception {
        // TODO
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

    private void closeSession(ChannelHandlerContext ctx) {}
//
//        override def channelRead0(ctx: ChannelHandlerContext, packet: MySQLCommandPacket): Unit = Future {
//            ensureSessionOpened(ctx)
//            packet match {
//                case pkt: MySQLComPingPacket => handlePing(ctx, pkt)
//                case pkt: MySQLComInitDbPacket => handleInitDb(ctx, pkt)
//                case pkt: MySQLComQuitPacket => handleQuit(ctx, pkt)
//                case pkt: MySQLComQueryPacket => handleQuery(ctx, pkt)
//                case bad => throw new UnsupportedOperationException(bad.getClass.getSimpleName)
//            }
//        } onComplete {
//            case Success(responsePackets) =>
//                responsePackets.foreach(ctx.channel.write)
//                ctx.channel.flush()
//            case Failure(cause) =>
//                exceptionCaught(ctx, cause)
//        }
//
//        def ensureSessionOpened(ctx: ChannelHandlerContext): Unit =
//        if (ctx.channel.attr(SESSION_HANDLE).get == null) synchronized {
//            if (ctx.channel.attr(SESSION_HANDLE).get == null) {
//                val sessionHandle = openSession(ctx)
//                ctx.channel.attr(SESSION_HANDLE).set(sessionHandle)
//                val connectionId = ctx.channel.attr(CONNECTION_ID).get
//                connIdToSessHandle.put(connectionId, sessionHandle)
//            }
//        }
//
//        def openSession(ctx: ChannelHandlerContext): SessionHandle = synchronized {
//            try {
//                val user = ctx.channel.attr(USER).get
//                val remoteIp = ctx.channel.attr(REMOTE_IP).get
//                // TODO parse SET command, save other variables at ChannelHandlerContext
//                val sessionConf = Option(ctx.channel.attr(DATABASE).get) match {
//                    case Some(db) => Map("use:database" -> db)
//                    case None => Map.empty[String, String]
//                }
//                // v1 is sufficient now, upgrade version when needed
//                val proto = TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V1
//                val sessionHandle = be.openSession(
//                        proto,
//                        user,
//                        "",
//                        remoteIp,
//                        sessionConf ++ Map(
//                                KYUUBI_SESSION_CONNECTION_URL_KEY -> connectionUrl,
//                                KYUUBI_SESSION_REAL_USER_KEY -> user))
//                sessionHandle
//            } catch {
//                case rethrow: Exception =>
//                    warn(s"Error opening session: ${rethrow.getMessage}")
//                    throw rethrow
//            }
//        }
//
//        def closeSession(ctx: ChannelHandlerContext): Unit = synchronized {
//            if (!closed) {
//                val handle = ctx.channel.attr(SESSION_HANDLE).get
//                info(s"Received request of closing $handle")
//                try be.closeSession(handle)
//      catch {
//                    case rethrow: Exception =>
//                        warn(s"Error closing session: ${rethrow.getMessage}")
//                        throw rethrow
//                } finally {
//                    val connectionId = ctx.channel.attr(CONNECTION_ID).getAndSet(null)
//                    ctx.channel.attr(SESSION_HANDLE).set(null)
//                    connIdToSessHandle.remove(connectionId)
//                }
//                closed = true
//                info(s"Finished closing $handle")
//            }
//        }
//
//        def handlePing(
//                ctx: ChannelHandlerContext,
//                pkg: MySQLComPingPacket): Seq[MySQLPacket] = {
//                MySQLOKPacket(1) :: Nil
//        }
//
//        def handleInitDb(
//                ctx: ChannelHandlerContext,
//                pkg: MySQLComInitDbPacket): Seq[MySQLPacket] = {
//                beExecuteStatement(ctx, s"use ${pkg.database}")
//                MySQLOKPacket(1) :: Nil
//        }
//
//        def handleQuit(
//                ctx: ChannelHandlerContext,
//                pkg: MySQLComQuitPacket): Seq[MySQLPacket] = {
//                closeSession(ctx)
//                MySQLOKPacket(1) :: Nil
//        }
//
//        def handleQuery(
//                ctx: ChannelHandlerContext,
//                pkg: MySQLComQueryPacket): Seq[MySQLPacket] = {
//                debug(s"Receive query: ${pkg.sql}")
//                executeStatement(ctx, pkg.sql).toPackets
//        }
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
//
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
}
