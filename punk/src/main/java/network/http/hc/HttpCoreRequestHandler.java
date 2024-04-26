package network.http.hc;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;

@Slf4j
public class HttpCoreRequestHandler implements Runnable {

    private final HttpService httpservice;
    private final HttpServerConnection conn;

    public HttpCoreRequestHandler(HttpService httpservice, HttpServerConnection conn) {
        this.httpservice = httpservice;
        this.conn = conn;
    }

    @Override
    public void run() {
        HttpContext context = new BasicHttpContext(null);
        try {
            while (!Thread.interrupted() && this.conn.isOpen()) {
                httpservice.handleRequest(this.conn, context);
            }
        } catch (ConnectionClosedException e) {
            log.warn("Client closed connection", e);
        } catch (IOException e) {
            log.error("I/O error: ", e);
        } catch (HttpException e) {
            log.error("Unrecoverable HTTP protocol violation: ", e);
        } finally {
            try {
                this.conn.shutdown();
            } catch (IOException ignore) {}
        }
    }
}