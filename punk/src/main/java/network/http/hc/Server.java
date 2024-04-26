package network.http.hc;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server extends Thread {
    private final ServerSocket serverSocket;
    private final SocketConfig config;
    private final HttpService service;
    private volatile boolean running;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public Server() throws IOException {
        serverSocket = new ServerSocket(8080);
        config = SocketConfig.custom()
                .setSoTimeout(120000)
                .setRcvBufSize(8 * 1024)
                .setSndBufSize(8 * 1024)
                .setTcpNoDelay(true)
                .build();

        HttpProcessor processor = new ImmutableHttpProcessor(
                new ResponseDate(),
                new ResponseServer(),
                new ResponseContent(),
                new ResponseConnControl());
        UriHttpRequestHandlerMapper mapper = new UriHttpRequestHandlerMapper();
        mapper.register("/hello*", null);
        mapper.register("*", new HttpFallbackHandler());
        service = new HttpService(
                processor,
                new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory(),
                mapper);
    }

    @Override
    public void run() {
        running = true;

        while (running && !Thread.interrupted()) {
            try {
                Socket socket = serverSocket.accept();
                DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8192);
                conn.bind(socket);

                HttpCoreRequestHandler handler = new HttpCoreRequestHandler(service, conn);
                executor.execute(handler);
            } catch (IOException e) {
                running = false;

                try {
                    serverSocket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
